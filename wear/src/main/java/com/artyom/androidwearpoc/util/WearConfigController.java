package com.artyom.androidwearpoc.util;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.UpdateRateMessage;

import org.greenrobot.eventbus.EventBus;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLING_RATE;

/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearConfigController {

    private int mSamplingRate;

    private WearSharedPrefsController mSharedPrefsController;

    private EventBus mEventBus;

    public WearConfigController(WearSharedPrefsController mSharedPrefsController, EventBus eventBus) {
        this.mSharedPrefsController = mSharedPrefsController;
        mEventBus = eventBus;
        loadConfigurationValues();
    }

    private void loadConfigurationValues() {
        mSamplingRate = mSharedPrefsController.getIntPreference(SAMPLING_RATE);
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
        mEventBus.post(new UpdateRateMessage(newSamplingRate));
    }

}
