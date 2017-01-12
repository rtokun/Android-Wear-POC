package com.artyom.androidwearpoc.util;

import com.google.android.gms.wearable.WearableListenerService;


/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class ConfigController extends WearableListenerService {

    private long mSamplingRate;

    private SharedPrefsController mSharedPrefsController;

    public ConfigController(SharedPrefsController mSharedPrefsController) {
        this.mSharedPrefsController = mSharedPrefsController;
    }


}
