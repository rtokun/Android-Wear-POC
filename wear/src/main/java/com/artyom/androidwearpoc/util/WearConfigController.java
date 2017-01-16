package com.artyom.androidwearpoc.util;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.UpdateChunkLimitMessage;
import com.artyom.androidwearpoc.shared.models.UpdateSamplingRateMessage;

import org.greenrobot.eventbus.EventBus;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLES_PER_CHUNK;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLING_RATE;

/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearConfigController {

    private int mSamplingRate;

    private int mSamplesPerChunk;

    private WearSharedPrefsController mSharedPrefsController;

    private EventBus mEventBus;

    public WearConfigController(WearSharedPrefsController mSharedPrefsController, EventBus eventBus) {
        this.mSharedPrefsController = mSharedPrefsController;
        mEventBus = eventBus;
        loadConfigurationValues();
    }

    private void loadConfigurationValues() {
        mSamplingRate = mSharedPrefsController.getIntPreference(SAMPLING_RATE);
        mSamplesPerChunk = mSharedPrefsController.getIntPreference(SAMPLES_PER_CHUNK);
    }

    public int getSamplesPerChunk() {
        if (mSamplesPerChunk == NUMBER_NOT_FOUND) {
            mSamplesPerChunk = DefaultConfiguration.DEFAULT_SAMPLES_PER_PACKAGE_LIMIT;
        }
        return mSamplesPerChunk;
    }

    public int getSamplingRate() {
        if (mSamplingRate == NUMBER_NOT_FOUND) {
            mSamplingRate = DefaultConfiguration.DEFAULT_SAMPLING_RATE_IN_HZ;
        }
        return mSamplingRate;
    }

    public int getSamplingRateMicrosecond() {
        int rateInHz = getSamplingRate();
        return 1000000 / rateInHz;
    }

    public void updateSamplingRate(int newSamplingRate) {
        if (newSamplingRate != mSamplingRate) {
            mSamplingRate = newSamplingRate;
            mSharedPrefsController.setIntPreference(SAMPLING_RATE, mSamplingRate);
            notifySamplingRateChanged(newSamplingRate);
        }
    }

    private void notifySamplingRateChanged(int newSamplingRate) {
        mEventBus.post(new UpdateSamplingRateMessage(newSamplingRate));
    }

    public void updateUpdateSamplesPerChunk(int newLimit) {
        if (newLimit != mSamplesPerChunk) {
            mSamplesPerChunk = newLimit;
            mSharedPrefsController.setIntPreference(SAMPLES_PER_CHUNK, mSamplesPerChunk);
            notifySamplesPerChunkChanged(newLimit);
        }
    }

    private void notifySamplesPerChunkChanged(int newLimit) {
        mEventBus.post(new UpdateChunkLimitMessage(newLimit));
    }
}
