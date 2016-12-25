package com.artyom.androidwearpoc;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MeasurementService extends Service implements SensorEventListener{

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    public final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;

    @Inject
    SensorManager mSensorManager;

    @Inject
    @Named("accelerometer")
    Sensor mAccelerometerSensor;

    @Inject
    Sensor mHeartRateSensor;

    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getmApplicationComponent().inject(this);
        initSensors();
        startMeasurement();
    }

    private void startMeasurement() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
