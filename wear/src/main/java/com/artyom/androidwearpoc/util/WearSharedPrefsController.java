package com.artyom.androidwearpoc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;

/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearSharedPrefsController {

    private Context mApplicationContext;

    private static final String MESSAGE_INDEX = "message_index";

    public WearSharedPrefsController(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public int getMessagePackageIndex() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        return sharedPreferences.getInt(MESSAGE_INDEX, 0);
    }

    public void setMessagePackageIndex(int newIndex) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        sharedPreferences.edit().putInt(MESSAGE_INDEX, newIndex).apply();
    }

    public Integer getIntPreference(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        return sharedPreferences.getInt(key, NUMBER_NOT_FOUND);
    }

    public void setIntPreference(String key, Integer value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        sharedPreferences.edit().putInt(key, value).apply();
    }

}
