package com.artyom.androidwearpoc.data.processing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.data.DataTransferHolder;
import com.artyom.androidwearpoc.measurement.MeasurementService;
import com.artyom.androidwearpoc.shared.enums.DataTransferType;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.SENSORS_MESSAGE;
import static com.artyom.androidwearpoc.shared.Configuration.DATA_TRANSFER_TYPE;

/**
 * Created by Artyom on 25/12/2016.
 */

public class DataProcessingService extends IntentService {

    @Inject
    DataTransferHolder mDataTransferHolder;

    private GoogleApiClient mGoogleApiClient;

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

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

        MyWearApplication.getApplicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("message received, checking extras");

        if (intent.getExtras() != null) {

            Bundle bundle = intent.getExtras();

            long messagePackageId = bundle.getLong(MeasurementService.MESSAGE_PACKAGE_ID, -1);

            // Retrieving message package from data holder matching the ID
            MessagePackage messagePackage = mDataTransferHolder
                    .getQueueOfMessagePackages()
                    .get(messagePackageId);

            if (messagePackageId != -1 && messagePackage != null) {

                Timber.d("message package id received successfully");
                processData(messagePackage);
                deleteProcessedPackageFromHolder(messagePackageId);
            } else {
                Timber.w("missing data to process");
            }
        } else {
            Timber.d("no extras. doing nothing");
        }
    }

    private void deleteProcessedPackageFromHolder(long messagePackageId) {
        mDataTransferHolder.getQueueOfMessagePackages().remove(messagePackageId);
    }

    private void processData(MessagePackage messagePackage) {

        PutDataRequest dataRequest;


        if (DATA_TRANSFER_TYPE.equals(DataTransferType.ASSET)) {
            dataRequest = packAssetData(messagePackage);
        } else {
            dataRequest = packRegularData(messagePackage);
        }

        if (dataRequest != null && validateConnection()) {
            sendData(dataRequest);
        }
    }

    private PutDataRequest packAssetData(MessagePackage messagePackage) {

        Asset assetMessage = createAssetFromMessagePackage(messagePackage);

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + System.currentTimeMillis());
        dataMap.getDataMap().putAsset(SENSORS_MESSAGE, assetMessage);

        return dataMap.asPutDataRequest();

    }

    private Asset createAssetFromMessagePackage(MessagePackage messagePackage) {
        byte[] messageInBytes = ParcelableUtil.marshall(messagePackage);
        return Asset.createFromBytes(messageInBytes);
    }

    private void sendData(PutDataRequest dataRequest) {

        Wearable.DataApi.putDataItem(mGoogleApiClient, dataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {

                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {

                        Timber.w("Sending sensor data. result %s",
                                (dataItemResult.getStatus().isSuccess() ? "success" : "failure"));

                        if (!dataItemResult.getStatus().isSuccess()) {
                            Timber.w("failure reason: %s", dataItemResult.getStatus().getStatusMessage());
                        }
                    }
                });
    }

    private PutDataRequest packRegularData(MessagePackage messagePackage) {

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
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        return result.isSuccess();
    }

}
