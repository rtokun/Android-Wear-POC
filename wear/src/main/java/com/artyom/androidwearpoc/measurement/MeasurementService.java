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
import com.artyom.androidwearpoc.dagger.qualifiers.AccelerometerSensor;
import com.artyom.androidwearpoc.dagger.qualifiers.HeartRateSensor;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MeasurementService extends Service implements SensorEventListener{

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    public final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;

    @Inject
    SensorManager mSensorManager;

    @Inject
    @AccelerometerSensor
    Sensor mAccelerometerSensor;

    @Inject
    @HeartRateSensor
    Sensor mHeartRateSensor;


    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getmApplicationComponent().inject(this);
        startMeasurement();
    }

    private void startMeasurement() {
        if (checkNotNull()){
            mSensorManager.registerListener(this, mAccelerometerSensor, 20000, 20000);
            mSensorManager.registerListener(this, mHeartRateSensor, 20000, 20000);
        }
    }

    private boolean checkNotNull() {
        return mSensorManager != null
                && mHeartRateSensor != null
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
    public void onSensorChanged(SensorEvent event) {
//        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
        Timber.d("");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}