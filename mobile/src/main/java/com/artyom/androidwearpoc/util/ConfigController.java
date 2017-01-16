package com.artyom.androidwearpoc.util;

import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.wear.communication.CommunicationController;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLES_PER_CHUNK;
import static com.artyom.androidwearpoc.shared.CommonConstants.SAMPLING_RATE;

/**
 * Created by Artyom on 12/01/2017.
 */

public class ConfigController {

    private CommunicationController mCommunicationController;

    private SharedPrefsController mSharedPrefsController;

    private Integer mSamplingRate;

    private Integer mSamplesPerChunk;

    public ConfigController(SharedPrefsController sharedPrefsController,
                            CommunicationController communicationController) {
        mCommunicationController = communicationController;
        mSharedPrefsController = sharedPrefsController;
        loadConfigurationValues();
    }

    private void loadConfigurationValues() {
        mSamplingRate = mSharedPrefsController.getIntPreference(SAMPLING_RATE);
        mSamplesPerChunk = mSharedPrefsController.getIntPreference(SAMPLES_PER_CHUNK);
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

    public int getSamplesPerChunk(){
        if (mSamplesPerChunk == NUMBER_NOT_FOUND) {
            mSamplesPerChunk = DefaultConfiguration.DEFAULT_SAMPLES_PER_PACKAGE_LIMIT;
        }
        return mSamplesPerChunk;
    }

    public void updateSamplesPerChunk(int newLimit){
        if (newLimit != mSamplesPerChunk) {
            mSamplesPerChunk = newLimit;
            mSharedPrefsController.setIntPreference(SAMPLES_PER_CHUNK, mSamplesPerChunk);
            notifySamplesPerChunkChanged(newLimit);
        }
    }

    private void notifySamplesPerChunkChanged(int newLimit) {
        mCommunicationController.updateSamplesPerChunk(newLimit);
    }


}
