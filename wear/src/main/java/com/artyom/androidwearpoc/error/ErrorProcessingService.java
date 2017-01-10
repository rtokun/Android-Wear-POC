package com.artyom.androidwearpoc.error;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import timber.log.Timber;

public class ErrorProcessingService extends IntentService {

    public ErrorProcessingService() {
        super("ErrorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GoogleApiClient mGoogleAppiClient = new GoogleApiClient.Builder(ErrorProcessingService.this)
                .addApi(Wearable.API)
                .build();
        mGoogleAppiClient.blockingConnect();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(intent.getSerializableExtra("exception"));

            byte[] exceptionData = bos.toByteArray();

            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/wear_error/");

            dataMapRequest.getDataMap().putString("board", Build.BOARD);
            dataMapRequest.getDataMap().putString("fingerprint", Build.FINGERPRINT);
            dataMapRequest.getDataMap().putString("model", Build.MODEL);
            dataMapRequest.getDataMap().putString("manufacturer", Build.MANUFACTURER);
            dataMapRequest.getDataMap().putString("product", Build.PRODUCT);
            dataMapRequest.getDataMap().putByteArray("exception", exceptionData);
            dataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());

            // Setting the exception data to the Data Layer
            Wearable.DataApi.putDataItem(mGoogleAppiClient, dataMapRequest.asPutDataRequest())
                    .setResultCallback(new ResultCallbacks<DataApi.DataItemResult>() {

                        @Override
                        public void onSuccess(@NonNull DataApi.DataItemResult dataItemResult) {
                            Timber.d("successfully sent an error from wear to mobile");
                        }

                        @Override
                        public void onFailure(@NonNull Status status) {
                            Timber.w("failed to send an error from wear to mobile");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException exx) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException exx) {
                // ignore close exception
            }
        }
    }
}