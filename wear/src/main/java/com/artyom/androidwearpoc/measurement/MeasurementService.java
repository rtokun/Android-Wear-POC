package com.artyom.androidwearpoc.measurement;

import android.app.Service;
import android.content.Context;
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
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.data.DataTransferHolder;
import com.artyom.androidwearpoc.data.processing.DataProcessingService;
import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MeasurementServiceStatus;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.models.UpdateChunkLimitMessage;
import com.artyom.androidwearpoc.shared.models.UpdateSamplingRateMessage;
import com.artyom.androidwearpoc.util.WearConfigController;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.MESSAGE_PACKAGE_ID;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.DEFAULT_SAMPLES_PER_PACKAGE_LIMIT;

/**
 * Created by Artyom-IDEO on 25-Dec-16. Serive in charge of measure the accelerometer and pack the
 * samples
 */

public class MeasurementService extends Service implements SensorEventListener {

    public final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    // For test only
//    public static final int SAMPLING_RATE_IN_MILLIS = 200000;

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

    @Inject
    WearConfigController mConfigController;

    private Sensor mAccelerometerSensor;

    private AccelerometerSampleData mLastEventData;

    protected HandlerThread handlerThread;

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        MyWearApplication.getApplicationComponent().inject(this);
        initSensors();
        mEventBus.register(this);
        startThread();
        acquireWakeLock();
        resetPackageValues();
        mSharedPrefsController.setMessagePackageIndex(0);
        startMeasurement();
        mEventBus.postSticky(new MeasurementServiceStatus(true));
    }

    private void acquireWakeLock() {
        if (mWakeLock == null || !mWakeLock.isHeld()) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorAdapter Lock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && !mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSamplingRateUpdated(UpdateSamplingRateMessage updateSamplingRateMessage) {
        stopMeasurement();
        startMeasurement();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSamplesPerPackageLimitUpdated(UpdateChunkLimitMessage chunkLimitMessage) {
        stopMeasurement();
        startMeasurement();
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
            int maxSamplesBuffer = 250 * mConfigController.getSamplingRateMicrosecond();
            mSensorManager.registerListener(this,
                    mAccelerometerSensor,
                    mConfigController.getSamplingRateMicrosecond(),
                    maxSamplesBuffer,
                    handler);
        } else {
            Timber.w("sensors are null");
        }
    }

    private void stopMeasurement() {
        mSensorManager.unregisterListener(this);
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
        stopMeasurement();
        releaseWakeLock();
        stopThread();
        mEventBus.unregister(this);
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

        AccelerometerSampleData newEventData = new AccelerometerSampleData(
                System.currentTimeMillis() + ((newEvent.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L),
                newEvent.values);

        if (DefaultConfiguration.LOG_EACH_SAMPLE) {
            logData(newEventData, calculateTimeDiff(newEventData));
        }

        addNewEventToPackage(newEventData);
        updateCurrentValues(newEventData);

        if (mCounter >= mConfigController.getSamplesPerChunk()) {
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

    private void logData(AccelerometerSampleData newEventData, long diff) {
//        if (diff > MAX_ALLOWED_SAMPLES_DIFF_IN_MILLIS
//                || diff < MIN_ALLOWED_SAMPLES_DIFF_IN_MILLIS) {
        Timber.d("new accelerometer event, timestamp: %s, time difference: %s milliseconds",
                newEventData.getTimestamp(), diff);
//        }
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
