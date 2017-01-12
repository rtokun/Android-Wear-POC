package com.artyom.androidwearpoc.util;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.wear.communication.CommunicationController;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLING_RATE;

/**
 * Created by Artyom on 12/01/2017.
 */

public class ConfigController {

    private CommunicationController mCommunicationController;

    private SharedPrefsController mSharedPrefsController;

    private Integer mSamplingRate;


    public ConfigController(SharedPrefsController sharedPrefsController,
                            CommunicationController communicationController) {
        mCommunicationController = communicationController;
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

    public void updateSamplingRate(int newSamplingRate) {
        if (newSamplingRate != mSamplingRate) {
            mSamplingRate = newSamplingRate;
            mSharedPrefsController.setIntPreference(SAMPLING_RATE, mSamplingRate);
            notifySamplingRateChanged(newSamplingRate);
        }
    }

    private void notifySamplingRateChanged(int newSamplingRate) {
        mCommunicationController.updateSamplingRate(newSamplingRate);
    }
}
