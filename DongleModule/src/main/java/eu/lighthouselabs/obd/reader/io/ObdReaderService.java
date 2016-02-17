package eu.lighthouselabs.obd.reader.io;

import java.util.ArrayList;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.util.Log;

import com.flightpathcore.utilities.SPHelper;

import eu.lighthouselabs.obd.reader.activity.ObdReaderConfigActivity;
import eu.lighthouselabs.obd.reader.command.ObdCommand;
import eu.lighthouselabs.obd.reader.io.ObdReaderServiceConnection.OBDServiceHandler;
import flightpath.com.donglemodule.DonglePreferences;
import flightpath.com.donglemodule.R;

public class ObdReaderService extends Service {
    public class ObdReaderServiceBinder extends Binder {
        ObdReaderService getService() {
            return ObdReaderService.this;
        }
    }

    private class ObdReaderServiceWorkerThread extends Thread {

       private ObdConnectThread t = null;

        public ObdReaderServiceWorkerThread(ObdConnectThread t) {
            this.t = t;
        }

        public void run() {
            try {
                t.start();
                long when = System.currentTimeMillis();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setContentTitle("OBD Service Running")
                                .setContentText("")
                                .setContentIntent(contentIntent)
                                .setSmallIcon(R.drawable.car)
                                .setWhen(when);

                notifyMan.notify(OBD_SERVICE_RUNNING_NOTIFY, mBuilder.build());
                t.join();
            } catch (Exception e) {
            } finally {
                notifyMan.cancel(OBD_SERVICE_RUNNING_NOTIFY);
                stopSelf();
            }
        }
    }

    public OBDServiceHandler additionalHandler;
    private ObdConnectThread connectThread = null;
    private final IBinder binder = new ObdReaderServiceBinder();
    private NotificationManager notifyMan = null;
    private Context context = null;
    private Intent notificationIntent = null;
    private PendingIntent contentIntent = null;
    public static final int COMMAND_ERROR_NOTIFY = 2;
    public static final int CONNECT_ERROR_NOTIFY = 3;
    public static final int OBD_SERVICE_RUNNING_NOTIFY = 4;
    public static final int OBD_SERVICE_ERROR_NOTIFY = 5;
    private ObdReaderServiceWorkerThread mObdReaderServiceWorkerThread;
    public boolean errorOnReadingCommand = false;

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopService();
        super.onTaskRemoved(rootIntent);
    }

    public void onCreate() {
        super.onCreate();
        notifyMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        context = getApplicationContext();
        notificationIntent = new Intent(this, ObdReaderService.class);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    }

    public void onDestroy() {
        super.onDestroy();
        stopService();
    }

    public boolean isRunning() {
        if (connectThread == null) {
            return false;
        }
        return connectThread.isAlive();
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean startService() {
        if (connectThread != null && connectThread.isAlive()) {
            return true;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceAddress = DonglePreferences.getPairedDevice(context);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support bluetooth", Toast.LENGTH_LONG).show();
            stopSelf();
            return false;
        }
        if (deviceAddress == null || "".equals(deviceAddress)) {
            Toast.makeText(this, "No bluetooth device selected", Toast.LENGTH_LONG).show();
            stopSelf();
            return false;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(this, "This device does not support GPS", Toast.LENGTH_LONG).show();
            stopSelf();
            return false;
        }
        int period = ObdReaderConfigActivity.getUpdatePeriod(prefs);
        double ve = ObdReaderConfigActivity.getVolumetricEfficieny(prefs);
        double ed = ObdReaderConfigActivity.getEngineDisplacement(prefs);
        boolean imperialUnits = prefs.getBoolean(ObdReaderConfigActivity.IMPERIAL_UNITS_KEY, false);
        boolean gps = prefs.getBoolean(ObdReaderConfigActivity.ENABLE_GPS_KEY, false);
        ArrayList<ObdCommand> cmds = ObdReaderConfigActivity.getObdCommands(prefs);

        BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        connectThread = new ObdConnectThread(dev, locationManager, this, period, ed, ve, imperialUnits, gps, cmds, additionalHandler);
        connectThread.setEngineDisplacement(ed);
        connectThread.setVolumetricEfficiency(ve);
        new ObdReaderServiceWorkerThread(connectThread).start();
        // mObdReaderServiceWorkerThread = new
        // ObdReaderServiceWorkerThread(connectThread);
        // mObdReaderServiceWorkerThread.start();

        return true;
    }

    public boolean stopService() {
        if (connectThread == null) {
            return true;
        }
        connectThread.cancel();
        connectThread.closeConnectThread();
        stopSelf();
        connectThread = null;
        mObdReaderServiceWorkerThread = null;
        removeNotifMsg();
        additionalHandler.onOBDDisconnected();
        return true;
    }

    public void popupWrongDevice() {
        Looper.prepare();
    }

    public void removeNotifMsg(){
        notifyMan.cancel(CONNECT_ERROR_NOTIFY);
        notifyMan.cancel(OBD_SERVICE_ERROR_NOTIFY);
        notifyMan.cancel(OBD_SERVICE_RUNNING_NOTIFY);
    }

    public void notifyMessage(String msg, String longMsg, int notifyId) {
        long when = System.currentTimeMillis();
//        Notification notification = new Notification(android.R.drawable.stat_notify_error, msg, when);
//        notification.setLatestEventInfo(context, msg, longMsg, contentIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setContentTitle(msg)
                .setContentText(longMsg)
                .setContentIntent(contentIntent)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setWhen(when);

        notifyMan.notify(notifyId, mBuilder.build());
    }

    public void setErrorOnReadingParam(boolean isError) {
        this.errorOnReadingCommand = isError;
        if (isError && this.isRunning()) {
            this.stopService();
        }
    }

    public Map<String, String> getDataMap() {
        if (connectThread != null) {
            return connectThread.getResults();
        }

        return null;
    }

}
