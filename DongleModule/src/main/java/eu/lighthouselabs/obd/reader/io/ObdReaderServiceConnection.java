package eu.lighthouselabs.obd.reader.io;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ObdReaderServiceConnection implements ServiceConnection {
    public interface OBDServiceHandler {
        void serviceConnected();
        void onOBDConnected();
        void serviceDisconnected();
        void onOBDDisconnected(Throwable error);
    }

    public ObdReaderService service = null;
    private final OBDServiceHandler obdHandler;

    public ObdReaderServiceConnection(OBDServiceHandler handler) {
        this.obdHandler = handler;
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("OBDSERVICECONNECTION", "ON SERVICE CONNECTED");
        this.service = ((ObdReaderService.ObdReaderServiceBinder) service).getService();
        this.service.additionalHandler = obdHandler;
        obdHandler.serviceConnected();
    }

    public void onServiceDisconnected(ComponentName name) {
        Log.d("OBDSERVICECONNECTION", "ON SERVICE DISCONNECTED");
        service = null;
        obdHandler.serviceDisconnected();
    }

    public ObdReaderService getService() {
        return service;
    }

    public boolean isRunning() {
        return service != null && service.isRunning();
    }
}
