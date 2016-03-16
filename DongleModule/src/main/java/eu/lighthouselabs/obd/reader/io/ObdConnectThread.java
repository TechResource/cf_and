package eu.lighthouselabs.obd.reader.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.flightpathcore.base.BaseApplication;

import eu.lighthouselabs.obd.reader.command.ObdCommand;
import eu.lighthouselabs.obd.reader.io.ObdReaderServiceConnection.OBDServiceHandler;
import flightpath.com.donglemodule.DonglePreferences;

public class ObdConnectThread extends Thread implements LocationListener {

    protected BluetoothDevice dev = null;
    protected BluetoothSocket sock = null;
    protected boolean stopThread = false;
    protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected InputStream in = null;
    protected OutputStream out = null;
    protected ArrayList<ObdCommand> cmds = null;
    protected HashMap<String, String> results = null;
    protected HashMap<String, Object> data = null;
    protected int updateCycle = 500;
    public ObdReaderService mService = null;
    protected Location currentLocation = null;
    protected LocationManager locationManager = null;
    protected double engineDisplacement = 1.0;
    protected double volumetricEfficiency = 1.0;
    protected boolean imperialUnits = false;
    protected final OBDServiceHandler additionalHandler;

    public ObdConnectThread(BluetoothDevice dev, LocationManager locationManager, final ObdReaderService service, int updateCycle, double engineDisplacement,
                            double volumetricEfficiency, boolean imperialUnits, boolean enableGps, ArrayList<ObdCommand> cmds, OBDServiceHandler connectionHandler) {
        this.dev = dev;
        this.cmds = cmds;
        this.updateCycle = updateCycle;
        this.mService = service;
        this.locationManager = locationManager;
        this.additionalHandler = connectionHandler;
        if (locationManager != null && enableGps) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
        }
        results = new HashMap<String, String>();
        data = new HashMap<String, Object>();
        this.volumetricEfficiency = volumetricEfficiency;
        this.engineDisplacement = engineDisplacement;
        this.imperialUnits = imperialUnits;
    }

    protected void startDevice() throws IOException, InterruptedException {
        if (sock != null && sock.isConnected()) {
            sock.close();
        }
        sock = this.dev.createRfcommSocketToServiceRecord(MY_UUID);
        sock.connect();
        if (additionalHandler != null) {
            additionalHandler.onOBDConnected();
        }
        in = sock.getInputStream();
        out = sock.getOutputStream();
        while (!stopThread) {
            ObdCommand echoOff = new ObdCommand("ate0", "echo off", "string", "string");
            String result = runCommand(echoOff).replace(" ", "");
            if (result != null && result.contains("OK")) {
                break;
            }
            Thread.sleep(1500);
        }
    }

    public void run() {
        Throwable error = null;

        try {
            startDevice();
            int cmdSize = cmds.size();
            for (int i = 0; i < cmdSize; i++) {
                String desc = cmds.get(i).getDesc();
                results.put(desc, "--");
                data.put(desc, -9999);
            }
            long lastSuccesCmdTimestamp = System.currentTimeMillis();
            for (int i = 0; !stopThread; i = ((i + 1) % cmdSize)) {
                if (i == 0) {
                    long obsTime = System.currentTimeMillis() / 1000;
                    results.put("Obs Time", Long.toString(obsTime));
                    data.put("Obs Time", obsTime);
                    Thread.sleep(updateCycle);
                }
                ObdCommand cmd = cmds.get(i);

                try {
                    cmd = getCopy(cmd); // make a copy because thread can only
                    // run once
                    String desc = cmd.getDesc();
                    String result = runCommand(cmd);
                    results.put(desc, result);
                    data.put(desc, cmd.getRawValue());
                    if (!result.isEmpty() && !result.equals("NODATA")) {
                        lastSuccesCmdTimestamp = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    Log.d("ObdConnectThread", "error " + e.getMessage());
                    e.printStackTrace();
                    error = e;
                    results.put(cmd.getDesc(), "--");
                } finally {
                    if (lastSuccesCmdTimestamp + 20000 < System.currentTimeMillis()) {
                        additionalHandler.onOBDDisconnected(error);
                    }
                    error = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            mService.notifyMessage("Bluetooth Connection Error", e.getMessage(), ObdReaderService.CONNECT_ERROR_NOTIFY);
            mService.popupWrongDevice();
            additionalHandler.onOBDDisconnected(e);

        } catch (Exception e) {
            e.printStackTrace();
            additionalHandler.onOBDDisconnected(e);
        } finally {
            closeConnectThread();
        }
    }

    public static ObdCommand getCopy(ObdCommand cmd) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return cmd.getClass().getConstructor(cmd.getClass()).newInstance(cmd);
    }

    public String runCommand(ObdCommand cmd) throws InterruptedException {
        cmd.setInputStream(in);
        cmd.setOutputStream(out);
        cmd.setDataMap(data);
        cmd.setConnectThread(this);
        cmd.start();
        while (!stopThread) {
            cmd.join(100);
            if (!cmd.isAlive()) {
                break;
            }
        }
        return cmd.formatResult();
    }

    public ArrayList<ObdCommand> getCmds() {
        return cmds;
    }

    public synchronized Map<String, String> getResults() {
        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();
            int speed = (int) currentLocation.getSpeed();
            long gtime = currentLocation.getTime() / 1000;
            results.put("Latitude", String.format("%.5f", lat));
            results.put("Longitude", String.format("%.5f", lon));
            results.put("GPS Speed", String.format("%d m/s", speed));
            results.put("GPS Time", Long.toString(gtime));
            data.put("Latitude", lat);
            data.put("Longitude", lon);
            data.put("GPS Speed", speed);
            data.put("GPS Time", gtime);
        }
        return results;
    }

    public void cancel() {
        stopThread = true;
    }

    public void closeConnectThread() {
        try {
            locationManager.removeUpdates(this);
        } catch (Exception e) {
        }
        try {
            stopThread = true;
            sock.close();
        } catch (Exception e) {
        }
    }

    public String getStackTrace(Exception e) {
        StringWriter strw = new StringWriter();
        PrintWriter ptrw = new PrintWriter(strw);
        e.printStackTrace(ptrw);
        return strw.toString();
    }

    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    public void onProviderDisabled(String provider) {
        mService.notifyMessage("GPS Unavailable", "GPS_PROVIDER disabled, please enable gps in your settings.", ObdReaderService.OBD_SERVICE_ERROR_NOTIFY);
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void setVolumetricEfficiency(double ve) {
        this.volumetricEfficiency = ve;
    }

    public void setEngineDisplacement(double ed) {
        this.engineDisplacement = ed;
    }

    public double getVolumetricEfficiency() {
        return volumetricEfficiency;
    }

    public double getEngineDisplacement() {
        return engineDisplacement;
    }

    public boolean getImperialUnits() {
        return imperialUnits;
    }
}