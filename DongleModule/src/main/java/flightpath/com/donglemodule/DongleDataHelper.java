package flightpath.com.donglemodule;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import eu.lighthouselabs.obd.reader.activity.ObdReaderConfigActivity;
import eu.lighthouselabs.obd.reader.command.ObdCommand;
import eu.lighthouselabs.obd.reader.config.ObdConfig;
import eu.lighthouselabs.obd.reader.io.ObdReaderService;
import eu.lighthouselabs.obd.reader.io.ObdReaderServiceConnection;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-13.
 */
public class DongleDataHelper implements ObdReaderServiceConnection.OBDServiceHandler{

    private List<DongleDataListener> listeners;
    private Map<String, String> currentDongleData;
    private ObdReaderServiceConnection serviceConnection = null;
    private Intent serviceIntent = null;
    private CollectDongleDataThread collectDongleDataThread = null;
    private BluetoothDevice pairedDevice = null;
    private List<DongleServiceListener> dongleServiceListeners;
    private boolean isDongleConnected = false;

    @Inject
    public DongleDataHelper(){
        listeners = new ArrayList<>();
        dongleServiceListeners = new ArrayList<>();
    }

    public void setDongleServiceListener(DongleServiceListener listener){
        this.dongleServiceListeners.add(listener);
    }

    public void startService(Activity activity){
        if(serviceIntent != null && serviceConnection != null && !serviceConnection.isRunning()){
            onDestroy(activity);
        }
        serviceIntent = new Intent(activity, ObdReaderService.class);
        serviceConnection = new ObdReaderServiceConnection(this);
        if(!serviceConnection.isRunning()){
            activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void setPairedDevice(BluetoothDevice device){
        this.pairedDevice = device;
    }

    private void startCollectingData(){
        if(serviceConnection != null && !serviceConnection.isRunning() && serviceConnection.getService() != null) {
            serviceConnection.getService().startService();
        }
    }

    public void onDestroy(Activity activity){
        if(serviceConnection != null && serviceConnection.isRunning()) {
            try {
                activity.unbindService(serviceConnection);
            }catch (Exception e){

            }
        }
        if(activity != null && serviceIntent != null) {
            activity.stopService(serviceIntent);
        }
        if(collectDongleDataThread != null){
            collectDongleDataThread.stop = true;
        }
    }

    public void onDataReceived(Map<String, String> dongleData){
        this.currentDongleData = dongleData;
        notifyAllListeners();
    }

    public void addListener(DongleDataListener listener){
        listeners.add(listener);
        listener.onDongleDataReceived(currentDongleData);
    }

    public void removeListener(DongleDataListener listener){
        while (listeners.remove(listener));
    }

    private void notifyAllListeners(){
        for (DongleDataListener l : listeners){
            l.onDongleDataReceived(currentDongleData);
        }
    }

    @Override
    public void serviceConnected() {
        if(serviceConnection != null && serviceConnection.getService() != null) {
            serviceConnection.getService().additionalHandler = this;
        }
        if(pairedDevice != null){
            startCollectingData();
        }
    }

    @Override
    public void onOBDConnected() {
        isDongleConnected = true;
        if(collectDongleDataThread == null){
            collectDongleDataThread = new CollectDongleDataThread();
        }
        if(!collectDongleDataThread.isAlive()){
            collectDongleDataThread.start();
        }
        for(DongleServiceListener dongleServiceListener : dongleServiceListeners) {
            if (dongleServiceListener != null) {
                dongleServiceListener.onDongleConnected();
            }
        }
    }

    @Override
    public void serviceDisconnected() {
        if(collectDongleDataThread != null){
            collectDongleDataThread.stop = true;
        }
    }

    @Override
    public void onOBDDisconnected() {
        isDongleConnected = false;
        if(collectDongleDataThread != null){
            collectDongleDataThread.stop = true;
        }
        for(DongleServiceListener dongleServiceListener : dongleServiceListeners) {
            if (dongleServiceListener != null) {
                dongleServiceListener.onDongleDisconnected();
            }
        }
        currentDongleData = new HashMap<>();
        currentDongleData = clearDataMap(currentDongleData);
    }

    public boolean isDongleConnected(){
        return isDongleConnected;
    }

    public void disconnectDevice() {
        serviceConnection.getService().stopService();
    }

    public Map<String, String> getCurrentData() {
        return currentDongleData;
    }

    public interface DongleDataListener{
        void onDongleDataReceived(Map<String, String> dongleData);
    }

    public interface DongleServiceListener{
        void onDongleConnected();
        void onDongleDisconnected();
    }

    private class CollectDongleDataThread extends Thread{
        private static final long sleepTime = 500;
        protected boolean stop = false;
        private ObdReaderService service = null;
        private Map<String, String> dataMap = null;

        @Override
        public void run() {
            service = serviceConnection.getService();
            while (!stop) {
                if(service != null){
                    dataMap = service.getDataMap();
                    if(dataMap == null){
                        dataMap = new HashMap<>();
                        dataMap = clearDataMap(dataMap);
                    }
                    onDataReceived(dataMap);
                }else{
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

    private Map<String, String> clearDataMap(Map<String, String> data){
        for(ObdCommand cmd : ObdConfig.getCommands()){
            data.put(cmd.getDesc(), "--");
        }
        return data;
    }

}
