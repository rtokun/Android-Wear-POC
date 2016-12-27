package com.artyom.androidwearpoc.wear.data;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.SENSORS_MESSAGE;

/**
 * Created by Artyom on 27/12/2016.
 */

public class DataReceiverService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private GoogleApiClient mGoogleApiClient;

    private String mLocalNodeId;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this.getApplicationContext(), this, this))
                .build()
                .googleApiClient();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Timber.d("events arrived");

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

        if (!validateGoogleClientConnection()){
            Timber.w("failed to connect Google client");
            return;
        }

        // Running through all the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED && isFromOtherNode(event)) {
                processData(event);
                deleteProcessedItemFromDataLayer(event);
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Timber.d("data event deleted, uri: %s", event.getDataItem().getUri());
            }
        }
    }

    private void deleteProcessedItemFromDataLayer(DataEvent event) {

        Uri dataItemUri = event.getDataItem().getUri();

        Timber.d("deleting data item, uri: %s", dataItemUri);
        Wearable.DataApi.deleteDataItems(mGoogleApiClient, dataItemUri);
    }

    private boolean isFromOtherNode(DataEvent event) {

        // Checking if it's the same node that has fired the event
        String node = event.getDataItem().getUri().getHost();

        if (node.equals(mLocalNodeId)) {
            Timber.d("Skipping Event - same receiver");
            return false;
        } else {
            return true;
        }
    }

    private void processData(DataEvent event) {
        Timber.d("processing data");

        byte[] data = DataMapItem
                .fromDataItem(event.getDataItem())
                .getDataMap()
                .getByteArray(SENSORS_MESSAGE);

        MessagePackage messagePackage = ParcelableUtil.unmarshall(data, MessagePackage.CREATOR);

        logValues(messagePackage);
    }

    private void logValues(MessagePackage messagePackage) {

        float batteryPercentage = messagePackage.getmBatteryPercentage();
        int size = messagePackage.getmAccelerometerSamples().size();
        AccelerometerSampleData firstSample = messagePackage.getmAccelerometerSamples().get(0);
        AccelerometerSampleData lastSample = messagePackage.getmAccelerometerSamples().get(size - 1);

        Timber.i("battery percentage: %s", batteryPercentage);
        Timber.i("amount of samples: %s", size);

        Timber.i("first sample: x = %s, y = %s, z = %s",
                firstSample.getValues()[0],
                firstSample.getValues()[1],
                firstSample.getValues()[2]);

        Timber.i("last sample: x = %s, y = %s, z = %s",
                lastSample.getValues()[0],
                lastSample.getValues()[1],
                lastSample.getValues()[2]);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }

    private boolean validateGoogleClientConnection() {

        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mGoogleApiClient
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        // If we have successfully connected Google Api client we can retrieve local node ID
        // and store it once. On every event we need to check if it wasn't fired from this local
        // node
        if (result.isSuccess()){
            mLocalNodeId = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await().getNode().getId();
        }

        return result.isSuccess();
    }

}
