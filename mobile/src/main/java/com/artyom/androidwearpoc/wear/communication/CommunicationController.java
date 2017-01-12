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

    public CommunicationController(Context applicationContext) {
        mContext = applicationContext;
    }

    public void updateSamplingRate(int samplingRate){
        Intent updateRateIntent = new Intent(mContext, CommunicationService.class);
        updateRateIntent.putExtra(ACTION, UPDATE_RATE_ACTION);
        updateRateIntent.putExtra(RATE, samplingRate);
        mContext.startService(updateRateIntent);
    }

}
