package com.artyom.androidwearpoc.measurement;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MeasurementService extends Service implements SensorEventListener{

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    @Inject
    SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;

    private SensorEvent mLastSensorEvent;


    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getApplicationComponent().inject(this);
        initSensors();
        startMeasurement();
    }

    private void initSensors() {
        Timber.d("initiating sensors");

        if (mAccelerometerSensor == null){
            mAccelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        }

    }

    private void startMeasurement() {
        Timber.d("starting measurement");
        if (checkNotNull()){
            Timber.d("sensors are valid, registering listeners");
            mSensorManager.registerListener(this, mAccelerometerSensor, 20000, 20000);
        } else {
            Timber.w("sensors are null");
        }
    }

    private boolean checkNotNull() {
        Timber.d("checking sensors validity");
        return mSensorManager != null
                && mAccelerometerSensor != null;
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
    public void onSensorChanged(SensorEvent newEvent) {

        long diff = calculateTimeDiff(newEvent);

        mLastSensorEvent = newEvent;

        Timber.d("sensor event occurred, sensor type: %s, accuracy: %s, timestamp: %s, values: " +
                "%s, time difference: %s", newEvent.sensor.getType(), newEvent.accuracy, newEvent.timestamp,
                newEvent.values, diff);
    }

    private long calculateTimeDiff(SensorEvent newEvent) {

        if (mLastSensorEvent == null){
            return 0;
        }

        return (newEvent.timestamp - mLastSensorEvent.timestamp)*1000;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
