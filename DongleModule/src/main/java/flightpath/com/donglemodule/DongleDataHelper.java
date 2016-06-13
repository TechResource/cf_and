package flightpath.com.donglemodule;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import eu.lighthouselabs.obd.reader.command.ObdCommand;
import eu.lighthouselabs.obd.reader.config.ObdConfig;
import eu.lighthouselabs.obd.reader.io.ObdReaderService;
import eu.lighthouselabs.obd.reader.io.ObdReaderServiceConnection;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-13.
 */
public class DongleDataHelper implements ObdReaderServiceConnection.OBDServiceHandler {

    private static final String AVG_FUEL_ECON = "avg_fuel_econ";
    private static final String AVG_FUEL_ECON_COUNT = "avg_fuel_econ_count";

    private List<DongleDataListener> listeners;
    private Map<String, String> currentDongleData;
    private ObdReaderServiceConnection serviceConnection = null;
    private Intent serviceIntent = null;
    private CollectDongleDataThread collectDongleDataThread = null;
    private BluetoothDevice pairedDevice = null;
    private List<DongleServiceListener> dongleServiceListeners;
    private boolean isDongleConnected = false;
    private Map<String, Double> avgFuelEcon;

    @Inject
    public DongleDataHelper() {
        listeners = new ArrayList<>();
        dongleServiceListeners = new ArrayList<>();
        avgFuelEcon = new HashMap<>();
    }

    public void setDongleServiceListener(DongleServiceListener listener) {
        this.dongleServiceListeners.add(listener);
    }

    public void startService(Activity activity) {
        if (serviceIntent != null && serviceConnection != null) {
            onDestroy(activity);
        }
        serviceIntent = new Intent(activity, ObdReaderService.class);
        serviceConnection = new ObdReaderServiceConnection(this);
        if (!serviceConnection.isRunning()) {
            activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void setPairedDevice(BluetoothDevice device) {
        this.pairedDevice = device;
    }

    private void startCollectingData() {
        if (serviceConnection != null && !serviceConnection.isRunning() && serviceConnection.getService() != null) {
            serviceConnection.getService().startService();
        }
    }

    public void onDestroy(Activity activity) {
        if (serviceConnection != null && serviceConnection.isRunning()) {
            try {
                activity.unbindService(serviceConnection);
            } catch (Exception e) {

            }
        }
        if (activity != null && serviceIntent != null) {
            activity.stopService(serviceIntent);
        }
        if (collectDongleDataThread != null) {
            collectDongleDataThread.stop = true;
        }
    }

    public void onDataReceived(Map<String, String> dongleData) {
        this.currentDongleData = dongleData;
        notifyAllListeners();
    }

    public void addListener(DongleDataListener listener) {
        listeners.add(listener);
        if(currentDongleData != null)
            listener.onDongleDataReceived(new HashMap<>(currentDongleData));
    }

    public void removeListener(DongleDataListener listener) {
        while (listeners.remove(listener));
    }

    private void notifyAllListeners() {
        Map<String, String> dataCpy = new HashMap<>(currentDongleData);
        List<DongleDataListener> listeners = new ArrayList<>(this.listeners);
        for (DongleDataListener l : listeners) {
            l.onDongleDataReceived(dataCpy);
        }
    }

    @Override
    public void serviceConnected() {
        if (serviceConnection != null && serviceConnection.getService() != null) {
            serviceConnection.getService().additionalHandler = this;
        }
        if (pairedDevice != null) {
            startCollectingData();
        }
    }

    @Override
    public void onOBDConnected() {
        isDongleConnected = true;
        if (collectDongleDataThread == null) {
            collectDongleDataThread = new CollectDongleDataThread();
        }
        if (!collectDongleDataThread.isAlive()) {
            collectDongleDataThread.start();
        }
        for (DongleServiceListener dongleServiceListener : dongleServiceListeners) {
            if (dongleServiceListener != null) {
                dongleServiceListener.onDongleConnected();
            }
        }
    }

    @Override
    public void serviceDisconnected() {
        if (collectDongleDataThread != null) {
            collectDongleDataThread.stop = true;
        }
    }

    @Override
    public void onOBDDisconnected(Throwable error, String extraInfo) {
        isDongleConnected = false;
        if (collectDongleDataThread != null) {
            collectDongleDataThread.stop = true;
        }
        for (DongleServiceListener dongleServiceListener : dongleServiceListeners) {
            if (dongleServiceListener != null) {
                dongleServiceListener.onDongleDisconnected(error, extraInfo);
            }
        }
        currentDongleData = new HashMap<>();
        currentDongleData = clearDataMap(currentDongleData);
    }

    public boolean isDongleConnected() {
        return isDongleConnected;
    }

    public void disconnectDevice() {
        serviceConnection.getService().stopService();
    }

    public Map<String, String> getCurrentData() {
        if (currentDongleData == null) {
            return null;
        }
        Map<String, String> dataCpy = new HashMap<>(currentDongleData);
        Double avg = null;
        if (avgFuelEcon.get(AVG_FUEL_ECON_COUNT) != null && avgFuelEcon.get(AVG_FUEL_ECON) != null && avgFuelEcon.get(AVG_FUEL_ECON_COUNT) != 0) {
            avg = avgFuelEcon.get(AVG_FUEL_ECON) / avgFuelEcon.get(AVG_FUEL_ECON_COUNT);
        }
        dataCpy.put(AVG_FUEL_ECON, String.format("%.1f mpg", avg));
        avgFuelEcon.clear();
        Log.d("DONGLE_DATA_TO_SEND", new Gson().toJson(dataCpy));
        return dataCpy;
    }

    public interface DongleDataListener {
        void onDongleDataReceived(Map<String, String> dongleData);
    }

    public interface DongleServiceListener {
        void onDongleConnected();

        void onDongleDisconnected(Throwable error, String extraInfo);
    }

    private class CollectDongleDataThread extends Thread {
        private static final long sleepTime = 50;
        protected boolean stop = false;
        private ObdReaderService service = null;
        private Map<String, String> dataMap = null;

        @Override
        public void run() {
            service = serviceConnection.getService();
            while (!stop) {
                if (service != null) {
                    dataMap = service.getDataMap();
                    if (dataMap == null) {
                        dataMap = new HashMap<>();
                        dataMap = clearDataMap(dataMap);
                    } else { // collect avg fuel econ
                        Double totalAvg = avgFuelEcon.get(AVG_FUEL_ECON);
                        if (totalAvg == null)
                            totalAvg = 0.0;
                        Double currentAvg = service.getCurrentFuelEcon();
                        if (currentAvg != null && currentAvg != 0.0) {
                            avgFuelEcon.put(AVG_FUEL_ECON, totalAvg + currentAvg);
                            Double currentCount = avgFuelEcon.get(AVG_FUEL_ECON_COUNT);
                            if (currentCount == null)
                                currentCount = 0.0;
                            currentCount += 1;
                            avgFuelEcon.put(AVG_FUEL_ECON_COUNT, currentCount);
                        }
                    }
                    onDataReceived(dataMap);
                } else {
                    dataMap = new HashMap<>();
                    dataMap = clearDataMap(dataMap);
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            collectDongleDataThread = null;
        }
    }

    private Map<String, String> clearDataMap(Map<String, String> data) {
        for (ObdCommand cmd : ObdConfig.getCommands()) {
            data.put(cmd.getDesc(), "--");
        }
        return data;
    }

}
