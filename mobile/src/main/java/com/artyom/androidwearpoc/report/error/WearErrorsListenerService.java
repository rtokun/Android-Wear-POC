package com.artyom.androidwearpoc.report.error;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.report.ReportController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */

public class WearErrorsListenerService extends WearableListenerService
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private GoogleApiClient mGoogleApiClient;

    private String mLocalNodeId;

    private ReportController mReportController;


    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this.getApplicationContext(), this, this))
                .build()
                .googleApiClient();

        mReportController = MyMobileApplication.getApplicationComponent()
                .getReportController();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

        if (!validateGoogleClientConnection()) {
            Timber.w("failed to connect Google client");
            return;
        }

        // Running through all the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED && isFromOtherNode(event)) {
                processData(event);
            }
        }
    }

    private void processData(DataEvent event) {

        DataMap map = DataMapItem
                .fromDataItem(event.getDataItem())
                .getDataMap();

        ByteArrayInputStream bis = new ByteArrayInputStream(map.getByteArray("exception"));

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);

            Throwable ex = (Throwable) ois.readObject();

            mReportController.setBool("wear_exception", true);
            mReportController.setString("board", map.getString("board"));
            mReportController.setString("board", map.getString("board"));
            mReportController.setString("fingerprint", map.getString("fingerprint"));
            mReportController.setString("model", map.getString("model"));
            mReportController.setString("manufacturer", map.getString("manufacturer"));
            mReportController.setString("product", map.getString("product"));

            Timber.d("sending new wearable crashlytics exception");
            mReportController.reportCustomEvent(ex);
        } catch (IOException | ClassNotFoundException e) {
            Timber.e(e);
        }
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

    private boolean validateGoogleClientConnection() {

        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mGoogleApiClient
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        // If we have successfully connected Google Api client we can retrieve local node ID
        // and store it once. On every event we need to check if it wasn't fired from this local
        // node
        if (result.isSuccess()) {
            mLocalNodeId = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await().getNode().getId();
        }

        return result.isSuccess();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
