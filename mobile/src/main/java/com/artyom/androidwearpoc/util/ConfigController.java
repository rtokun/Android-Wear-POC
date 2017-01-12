package com.artyom.androidwearpoc.util;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;

import static com.artyom.androidwearpoc.util.SharedPrefsController.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.util.SharedPrefsController.SAMPLING_RATE;

/**
 * Created by Artyom on 12/01/2017.
 */

public class ConfigController {

    private SharedPrefsController mSharedPrefsController;

    private Integer mSamplingRate;


    public ConfigController(SharedPrefsController sharedPrefsController) {
        mSharedPrefsController = sharedPrefsController;
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

    public int getSamplingRateInHz() {
        return getSamplingRate();
    }

    public void updateSamplingRate(int newSamplingRate) {
        if (newSamplingRate != mSamplingRate) {
            mSamplingRate = newSamplingRate;
            mSharedPrefsController.setIntPreference(SAMPLING_RATE, mSamplingRate);
            notifySamplingRateChanged();
        }
    }

    private void notifySamplingRateChanged() {

    }
}
