package com.artyom.androidwearpoc.data.processing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.measurement.MeasurementService;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Artyom on 25/12/2016.
 */

public class DataProcessingService extends IntentService {

    private GoogleApiClient mGoogleApiClient;

    private static final long CLIENT_CONNECTION_TIMEOUT = 15000;

    private static final String SENSORS_MESSAGE = "sensors_message";

    public DataProcessingService() {
        super("DataProcessingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this))
                .build()
                .googleApiClient();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("message received, checking extras");

        if (intent.getExtras() != null) {

            Bundle bundle = intent.getExtras();

            ArrayList<AccelerometerSampleData> accelerometerSamples = bundle
                    .getParcelableArrayList(MeasurementService.ACCELEROMETER_SAMPLES);
            float batteryPercentage = bundle
                    .getFloat(MeasurementService.BATTERY_PERCENTAGE, -1);

            if (accelerometerSamples != null || batteryPercentage != -1) {
                Timber.d("data received successfully");
                processData(accelerometerSamples, batteryPercentage);
            } else {
                Timber.w("missing data to process");
            }
        } else {
            Timber.d("no extras. doing nothing");
        }
    }

    private void processData(ArrayList<AccelerometerSampleData> accelerometerSamples, float batteryPercentage) {

        PutDataRequest dataRequest = packData(accelerometerSamples, batteryPercentage);

        if (dataRequest != null
                && validateConnection()) {
            sendData(dataRequest);
        }
    }

    private void sendData(PutDataRequest dataRequest) {

        Wearable.DataApi.putDataItem(mGoogleApiClient, dataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {

                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        Timber.d("Sending sensor data. result %s",
                                (dataItemResult.getStatus().isSuccess() ? "success" : "failure"));
                    }
                });
    }

    private PutDataRequest packData(ArrayList<AccelerometerSampleData> accelerometerSamples, float batteryPercentage) {

        MessagePackage messagePackage = new MessagePackage();
        messagePackage.setAccelerometerSamples(accelerometerSamples);
        messagePackage.setBatteryPercentage(batteryPercentage);

        byte[] sensorsMessageByteArray = ParcelableUtil.marshall(messagePackage);

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + System.currentTimeMillis());
        dataMap.getDataMap().putByteArray(SENSORS_MESSAGE, sensorsMessageByteArray);

        return dataMap.asPutDataRequest();
    }

    private boolean validateConnection() {

        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mGoogleApiClient
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

}
