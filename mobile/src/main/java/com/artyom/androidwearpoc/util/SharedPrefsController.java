package com.artyom.androidwearpoc.util;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.artyom.androidwearpoc.model.MessageData;
import com.artyom.androidwearpoc.shared.models.MessagePackage;

import javax.inject.Inject;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;
import static com.artyom.androidwearpoc.shared.CommonConstants.STRING_NOT_FOUND;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */
public class SharedPrefsController {

    private Context mApplicationContext;

    public static final String LAST_MESSAGE_DATA = "wearable_message_data";

    @Inject
    public SharedPrefsController(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;
    }

    public void saveMessage(MessagePackage message) {
        MessageData messageData = mapWearPackageToMessageData(message);
        String serializedMessageData = serializeToString(messageData);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        Timber.d("saving message data to shared preferences, message data: %s", serializedMessageData);
        sharedPreferences.edit()
                .putString(LAST_MESSAGE_DATA, serializedMessageData).commit();
    }

    public MessageData getLastMessage() {
        MessageData messageData = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);

        String serializedMessageData = sharedPreferences.getString(LAST_MESSAGE_DATA, null);
        if (serializedMessageData != null) {
            messageData = deserializeFromString(serializedMessageData, MessageData.class);
        }

        return messageData;
    }

    private <T> T deserializeFromString(String serializedMessageData, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(serializedMessageData, type);
    }

    private String serializeToString(MessageData messageData) {
        Gson gson = new Gson();
        return gson.toJson(messageData);
    }

    private MessageData mapWearPackageToMessageData(MessagePackage message) {
        int samplesAmount = message.getAccelerometerSamples().size();

        MessageData messageData = new MessageData();
        messageData.setPackageSize(samplesAmount);
        messageData.setFirstSampleTimestamp(message.getAccelerometerSamples()
                .get(0)
                .getTimestamp());
        messageData.setLastSampleTimestamp(message.getAccelerometerSamples()
                .get(samplesAmount - 1)
                .getTimestamp());
        messageData.setPackageIndex(message.getIndex());

        return messageData;
    }

    public String getStringPreference(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        return sharedPreferences.getString(key, STRING_NOT_FOUND);
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