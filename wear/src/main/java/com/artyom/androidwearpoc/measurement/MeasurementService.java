package com.artyom.androidwearpoc.measurement;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.data.processing.DataProcessingService;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MeasurementService extends Service implements SensorEventListener {

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    private static final int SAMPLES_PER_PACKAGE_LIMIT = 3125;

    public static final String ACCELEROMETER_SAMPLES = "accelerometer_samples";

    public static final String BATTERY_PERCENTAGE = "battery_percentage";

    private static int mCounter;

    private ArrayList<AccelerometerSampleData> mAccelerometerSensorSamples;

    @Inject
    SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;

    private AccelerometerSampleData mLastEventData;

    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getApplicationComponent().inject(this);
        initSensors();
        resetPackageValues();
        startMeasurement();
    }

    private void resetPackageValues() {
        mCounter = 0;
        mAccelerometerSensorSamples = new ArrayList<>();
    }

    private void initSensors() {
        Timber.d("initiating sensors");

        if (mAccelerometerSensor == null) {
            mAccelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        }

    }

    private void startMeasurement() {
        Timber.d("starting measurement");
        if (checkNotNull()) {
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

        AccelerometerSampleData newEventData = new AccelerometerSampleData(
                newEvent.timestamp,
                newEvent.values);

        logNewEventData(newEventData, calculateTimeDiff(newEventData));

        if (mCounter <= SAMPLES_PER_PACKAGE_LIMIT) {
            addNewEventToPackage(newEventData);
            updateCurrentValues(newEventData);
        } else {
            float batteryPercentage = getBatteryStatus();
            sendPackageToMobileDevice(batteryPercentage);
            resetPackageValues();
        }
    }

    private void sendPackageToMobileDevice(float batteryPercentage) {
        Intent sendPackageIntent = new Intent(this, DataProcessingService.class);
        sendPackageIntent.putParcelableArrayListExtra(ACCELEROMETER_SAMPLES,
                mAccelerometerSensorSamples);
        sendPackageIntent.putExtra(BATTERY_PERCENTAGE, batteryPercentage);

    }

    private float getBatteryStatus() {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        float batteryPercentage;
        if (batteryStatus != null){
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPercentage = level / (float)scale;
        } else {
            Timber.w("failed to retrieve battery percentage");
            batteryPercentage = -1;
        }

        return batteryPercentage;
    }


    private void addNewEventToPackage(AccelerometerSampleData newEventData) {
        mAccelerometerSensorSamples.add(newEventData);
    }


    private void updateCurrentValues(AccelerometerSampleData newEventData) {
        mLastEventData = newEventData;
        mCounter++;
    }

    private void logNewEventData(AccelerometerSampleData newEventData, long diff) {
        Timber.d("sensor event occurred, sensor type: %s, accuracy: %s, timestamp: %s, values: " +
                        "%s, time difference: %s",
                newEventData.getTimestamp(),
                newEventData.getValues(),
                diff);
    }

    private long calculateTimeDiff(AccelerometerSampleData newEventData) {

        if (mLastEventData == null) {
            return 0;
        }

        return (newEventData.getTimestamp() - mLastEventData.getTimestamp());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
