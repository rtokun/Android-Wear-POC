package com.artyom.androidwearpoc.wear.communication;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Artyom-IDEO on 12-Jan-17.
 */
public class CommunicationController {

    private Context mContext;

    public static final String ACTION = "action_name";
    public static final String RATE = "sampling_rate";
    public static final String UPDATE_RATE_ACTION = "update_rate";
    public static final String UPDATE_SAMPLES_PER_CHUNK_ACTION = "samples_per_chunk";
    public static final String START_MEASUREMENT = "start_measurement";
    public static final String RESET_MEASUREMENT = "reset_measurement";
    public static final String AMOUNT = "amount";

    public CommunicationController(Context applicationContext) {
        mContext = applicationContext;
    }

    public void updateSamplingRate(int samplingRate){
        Intent updateRateIntent = new Intent(mContext, CommunicationService.class);
        updateRateIntent.putExtra(ACTION, UPDATE_RATE_ACTION);
        updateRateIntent.putExtra(RATE, samplingRate);
        mContext.startService(updateRateIntent);
    }

    public void updateSamplesPerChunk(int newLimit) {
        Intent updateSamplesPerChunkIntent = new Intent(mContext, CommunicationService.class);
        updateSamplesPerChunkIntent.putExtra(ACTION, UPDATE_SAMPLES_PER_CHUNK_ACTION);
        updateSamplesPerChunkIntent.putExtra(AMOUNT, newLimit);
        mContext.startService(updateSamplesPerChunkIntent);
    }

    public void startMeasurementService() {
        Intent startServiceIntent = new Intent(mContext, CommunicationService.class);
        startServiceIntent.putExtra(ACTION, START_MEASUREMENT);
        mContext.startService(startServiceIntent);
    }

    public void resetMeasurementService() {
        Intent resetServiceIntent = new Intent(mContext, CommunicationService.class);
        resetServiceIntent.putExtra(ACTION, RESET_MEASUREMENT);
        mContext.startService(resetServiceIntent);
    }
}
