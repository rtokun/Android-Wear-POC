package com.artyom.androidwearpoc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearSharedPrefsController {

    private Context mAppContext;

    private static final String MESSAGE_INDEX = "message_index";

    public WearSharedPrefsController(Context applicationContext) {
        mAppContext = applicationContext;
    }

    public int getMessagePackageIndex() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mAppContext);
        return sharedPreferences.getInt(MESSAGE_INDEX, 0);
    }

    public void setMessagePackageIndex(int newIndex) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mAppContext);
        sharedPreferences.edit().putInt(MESSAGE_INDEX, newIndex).apply();
    }
}
