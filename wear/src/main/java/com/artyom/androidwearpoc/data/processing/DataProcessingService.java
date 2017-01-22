package com.artyom.androidwearpoc.data.processing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.WearGoogleApiModule;
import com.artyom.androidwearpoc.data.DataTransferHolder;
import com.artyom.androidwearpoc.shared.enums.DataTransferType;
import com.artyom.androidwearpoc.shared.models.SamplesChunk;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.MESSAGE_PACKAGE_ID;
import static com.artyom.androidwearpoc.shared.CommonConstants.SENSORS_MESSAGE;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.DATA_TRANSFER_TYPE;

/**
 * Created by Artyom on 25/12/2016.
 */

public class DataProcessingService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @Inject
    DataTransferHolder mDataTransferHolder;

    @Inject
    WearSharedPrefsController mSharedPrefsController;

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
                .wearGoogleApiModule(new WearGoogleApiModule(this, this, this))
                .build()
                .googleApiClient();

        ((MyWearApplication)getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("message received, checking extras");

        if (intent.getExtras() != null) {

            Bundle bundle = intent.getExtras();

            long messagePackageId = bundle.getLong(MESSAGE_PACKAGE_ID, -1);

            // Retrieving message package from data holder matching the ID
            SamplesChunk samplesChunk = mDataTransferHolder
                    .getQueueOfMessagePackages()
                    .get(messagePackageId);

            if (messagePackageId != -1 && samplesChunk != null) {

                Timber.d("message package id received successfully");
                processData(samplesChunk);

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

    private void processData(SamplesChunk samplesChunk) {

        PutDataRequest dataRequest;

        // For debugging purposes. We creating the index and attaching it to every
        // message is sent to mobile. That way we can track if any of messages wasn't received on
        // mobile side
        int index = getIndex();
        samplesChunk.setIndex(index);
        Timber.d("attached index %s to package", index);
        index++;
        updateIndex(index);

        if (DATA_TRANSFER_TYPE.equals(DataTransferType.ASSET)) {
            dataRequest = packAssetData(samplesChunk);
        } else {
            dataRequest = packRegularData(samplesChunk);
        }

        if (dataRequest != null && validateConnection()) {
            Timber.d("sending package, package index: %s, package amount: %s", samplesChunk
                    .getIndex(), samplesChunk.getAccelerometerSamples().size());
            sendData(dataRequest);
        }
    }

    private void updateIndex(int index) {
        mSharedPrefsController.setChunkIndex(index);
    }

    private int getIndex() {
        return mSharedPrefsController.getChunkIndex();
    }

    private PutDataRequest packAssetData(SamplesChunk samplesChunk) {

        Asset assetMessage = createAssetFromMessagePackage(samplesChunk);

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + System.currentTimeMillis());
        dataMap.getDataMap().putAsset(SENSORS_MESSAGE, assetMessage);

        return dataMap.asPutDataRequest();

    }

    private Asset createAssetFromMessagePackage(SamplesChunk samplesChunk) {
        byte[] messageInBytes = ParcelableUtil.marshall(samplesChunk);
        return Asset.createFromBytes(messageInBytes);
    }

    private void sendData(PutDataRequest dataRequest) {

        Wearable.DataApi.putDataItem(mGoogleApiClient, dataRequest)
                .setResultCallback(new ResultCallback<DataItemResult>() {

                    @Override
                    public void onResult(@NonNull DataItemResult dataItemResult) {

                        Timber.w("Sending sensor data. result %s",
                                (dataItemResult.getStatus().isSuccess() ? "success" : "failure"));

                        if (!dataItemResult.getStatus().isSuccess()) {
                            Timber.w("failure reason: %s", dataItemResult.getStatus().getStatusMessage());
                        }
                    }
                });
    }

    private PutDataRequest packRegularData(SamplesChunk samplesChunk) {

        byte[] sensorsMessageByteArray = ParcelableUtil.marshall(samplesChunk);

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("connected to google client");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, cause: %s", connectionResult.getErrorMessage());
    }
}
