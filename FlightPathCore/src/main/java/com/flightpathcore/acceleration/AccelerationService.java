package com.flightpathcore.acceleration;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-05.
 */
public class AccelerationService extends Service implements SensorEventListener {
    public static String tag = "ACCELERATION_SERVICE";
    private SensorManager sensorManager;
    private long lastUpdate;
    private final IBinder mBinder = new MyBinder();
    private float currentAcceleration = 0f;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (actualTime - lastUpdate < 500) {
            return;
        }
        lastUpdate = actualTime;
        if(currentAcceleration < accelationSquareRoot) {
            currentAcceleration = accelationSquareRoot;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class MyBinder extends Binder {
        public AccelerationService getService() {
            return AccelerationService.this;
        }
    }

    public float getCurrentAcceleration(){
        float acc = currentAcceleration;
        currentAcceleration = 0f;
        return acc;
    }

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
