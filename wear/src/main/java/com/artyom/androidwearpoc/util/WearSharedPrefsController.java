package com.artyom.androidwearpoc.util;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.artyom.androidwearpoc.shared.models.ChunkData;
import com.artyom.androidwearpoc.shared.models.SamplesChunk;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.LAST_MESSAGE_DATA;
import static com.artyom.androidwearpoc.shared.CommonConstants.NUMBER_NOT_FOUND;

/**
 * Created by Artyom-IDEO on 11-Jan-17.
 */
public class WearSharedPrefsController {

    private Context mApplicationContext;

    private static final String CHUNK_INDEX = "message_index";

    public WearSharedPrefsController(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public ChunkData getLastChunk() {
        ChunkData chunkData = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);

        String serializedMessageData = sharedPreferences.getString(LAST_MESSAGE_DATA, null);
        if (serializedMessageData != null) {
            chunkData = deserializeFromString(serializedMessageData, ChunkData.class);
        }

        return chunkData;
    }

    public void saveMessage(SamplesChunk message) {
        ChunkData chunkData = mapWearPackageToMessageData(message);
        String serializedMessageData = serializeToString(chunkData);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        Timber.d("saving message data to shared preferences, message data: %s", serializedMessageData);
        sharedPreferences.edit()
                .putString(LAST_MESSAGE_DATA, serializedMessageData).commit();
    }

    private ChunkData mapWearPackageToMessageData(SamplesChunk message) {
        int samplesAmount = message.getAccelerometerSamples().size();

        ChunkData chunkData = new ChunkData();
        chunkData.setPackageSize(samplesAmount);
        chunkData.setFirstSampleTimestamp(message.getAccelerometerSamples()
                .get(0)
                .getTimestamp());
        chunkData.setLastSampleTimestamp(message.getAccelerometerSamples()
                .get(samplesAmount - 1)
                .getTimestamp());
        chunkData.setPackageIndex(message.getIndex());

        return chunkData;
    }

    private <T> T deserializeFromString(String serializedMessageData, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(serializedMessageData, type);
    }

    private String serializeToString(ChunkData chunkData) {
        Gson gson = new Gson();
        return gson.toJson(chunkData);
    }

    public int getChunkIndex() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        return sharedPreferences.getInt(CHUNK_INDEX, 0);
    }

    public void setChunkIndex(int newIndex) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mApplicationContext);
        sharedPreferences.edit().putInt(CHUNK_INDEX, newIndex).apply();
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
