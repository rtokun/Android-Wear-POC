package com.artyom.androidwearpoc.data.processing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
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

    private static final String MESSAGE_INDEX = "message_index";

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

        // For debugging purposes. We creating the index and attaching it to every
        // message is sent to mobile. That way we can track if any of messages wasn't received on
        // mobile side
        int index = getIndexFromDataLayer();
        messagePackage.setIndex(index);
        Timber.d("attached index %s to package", index);
        index++;
        updateIndexInDataLayer(index);

        if (DATA_TRANSFER_TYPE.equals(DataTransferType.ASSET)) {
            dataRequest = packAssetData(messagePackage);
        } else {
            dataRequest = packRegularData(messagePackage);
        }

        if (dataRequest != null && validateConnection()) {
            Timber.d("sending package, package index: %s, package amount: %s", messagePackage
                    .getIndex(), messagePackage.getAccelerometerSamples().size());
            sendData(dataRequest);
        }
    }

    private void updateIndexInDataLayer(int index) {

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/messages_index");
        putDataMapRequest.getDataMap().putInt(MESSAGE_INDEX, index);

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest).await(30, TimeUnit.SECONDS);
    }

    private int getIndexFromDataLayer() {

        try {

            validateConnection();

            // Creating URI to store the index to this path
            Uri uri = new Uri.Builder()
                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .path("/messages_index")
                    .build();

            // Retrieving data item related to that path
            DataItemBuffer dataItemBuffer = Wearable.DataApi.getDataItems(mGoogleApiClient,
                    uri).await(30, TimeUnit.SECONDS);

            // If the data was set to this path once we will retrieve the data object and take the
            // index from it
            if (dataItemBuffer.getStatus().isSuccess()
                    && dataItemBuffer.get(0) != null) {

                DataMap dataMap = DataMapItem
                        .fromDataItem(dataItemBuffer.get(0))
                        .getDataMap();

                return dataMap.getInt(MESSAGE_INDEX, 0);
            } else {
                Timber.e("failed to get index from data layer");
                return 0;
            }
        } catch (Exception e) {
            Timber.e("Failed to retrieve data from Data Layer, the reason: ", e);
            return -1;
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
