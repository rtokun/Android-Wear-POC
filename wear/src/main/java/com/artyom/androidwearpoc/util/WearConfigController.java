package com.artyom.androidwearpoc.util;

import com.google.android.gms.wearable.WearableListenerService;


/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearConfigController extends WearableListenerService {

    private long mSamplingRate;

    private WearSharedPrefsController mSharedPrefsController;

    public WearConfigController(WearSharedPrefsController mSharedPrefsController) {
        this.mSharedPrefsController = mSharedPrefsController;
    }


}
