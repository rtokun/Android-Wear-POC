package com.artyom.androidwearpoc.measurement;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.data.DataTransferHolder;
import com.artyom.androidwearpoc.data.processing.DataProcessingService;
import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MeasurementServiceStatus;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.DEFAULT_SAMPLES_PER_PACKAGE_LIMIT;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.MIN_ALLOWED_SAMPLES_DIFF_IN_MILLIS;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MeasurementService extends Service implements SensorEventListener {

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    public static final String MESSAGE_PACKAGE_ID = "message_package_id";

    private static int mCounter;

    private ArrayList<AccelerometerSampleData> mAccelerometerSensorSamples;

    @Inject
    SensorManager mSensorManager;

    @Inject
    DataTransferHolder mDataTransferHolder;

    @Inject
    EventBus mEventBus;

    @Inject
    WearSharedPrefsController mSharedPrefsController;

    private Sensor mAccelerometerSensor;

    private AccelerometerSampleData mLastEventData;

    protected HandlerThread handlerThread;

    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getApplicationComponent().inject(this);
        initSensors();
        startThread();
        resetPackageValues();
        mSharedPrefsController.setMessagePackageIndex(0);
        startMeasurement();
        mEventBus.postSticky(new MeasurementServiceStatus(true));
    }

    private void startThread() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread(this.getClass().getSimpleName() + "Thread");
        }
        if (handlerThread.getState() == Thread.State.NEW) {
            handlerThread.start();
        }
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
            Handler handler = new Handler(handlerThread.getLooper());
            // This buffer is max 300 on Moto 360, so we use 250;
            int maxSamplesBuffer = 300 * SENSOR_DELAY_GAME;
            mSensorManager.registerListener(this,
                    mAccelerometerSensor,
                    SENSOR_DELAY_GAME,
                    maxSamplesBuffer,
                    handler);
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
        mSensorManager.unregisterListener(this);
        stopThread();
        mEventBus.postSticky(new MeasurementServiceStatus(false));
    }

    private void stopThread() {
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent newEvent) {

        long timestampInMillis = TimeUnit.NANOSECONDS.toMillis(newEvent.timestamp);

        AccelerometerSampleData newEventData = new AccelerometerSampleData(
                timestampInMillis,
                newEvent.values);

        if (DefaultConfiguration.LOG_EACH_SAMPLE) {
            logOutOfRangeEventData(newEventData, calculateTimeDiff(newEventData));
        }

        if (mCounter < DEFAULT_SAMPLES_PER_PACKAGE_LIMIT) {
            addNewEventToPackage(newEventData);
            updateCurrentValues(newEventData);
        } else {
            float batteryPercentage = getBatteryStatus();
            sendPackageToMobileDevice(batteryPercentage);
            resetPackageValues();
        }
    }

    private void sendPackageToMobileDevice(float batteryPercentage) {
        Timber.i("sending package to processing service");

        long messagePackageId = System.currentTimeMillis();
        MessagePackage messagePackage = createMessagePackage(mAccelerometerSensorSamples, batteryPercentage);

        // Sending package in singleton holder
        mDataTransferHolder.getQueueOfMessagePackages().put(messagePackageId, messagePackage);

        Intent sendPackageIntent = new Intent(this, DataProcessingService.class);
        sendPackageIntent.putExtra(MESSAGE_PACKAGE_ID, messagePackageId);
        startService(sendPackageIntent);
    }

    private MessagePackage createMessagePackage(ArrayList<AccelerometerSampleData> mAccelerometerSensorSamples, float batteryPercentage) {
        MessagePackage messagePackage = new MessagePackage();
        messagePackage.setAccelerometerSamples(mAccelerometerSensorSamples);
        messagePackage.setBatteryPercentage(batteryPercentage);
        return messagePackage;
    }

    private float getBatteryStatus() {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        float batteryPercentage;
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPercentage = level / (float) scale;
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

    private void logOutOfRangeEventData(AccelerometerSampleData newEventData, long diff) {
        if (diff > MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS
                || diff < MIN_ALLOWED_SAMPLES_DIFF_IN_MILLIS) {
            Timber.d("new accelerometer event, timestamp: %s, time difference: %s milliseconds",
                    newEventData.getTimestamp(), diff);
        }
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
