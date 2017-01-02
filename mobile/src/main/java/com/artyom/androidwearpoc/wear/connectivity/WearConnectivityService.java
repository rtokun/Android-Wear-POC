package com.artyom.androidwearpoc.wear.connectivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;

import java.util.Set;

import timber.log.Timber;

import static com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusIndicatorType.ConnectedToWatch;
import static com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusIndicatorType.NotConnectedToWatch;
import static com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusIndicatorType.Unknown;
import static com.google.android.gms.wearable.CapabilityApi.FILTER_REACHABLE;

/**
 * Created by Artyom on 23/12/2016.
 */

public class WearConnectivityService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener {

    public static final String WATCH_CAPABILITY = "fox_watch_capability";

    private ConnectivityStatusNotificationController mConnectivityStatusNotificationController;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        initDependencies();
        mGoogleApiClient.connect();
        startForeground(ConnectivityStatusNotificationController.CONNECTIVITY_STATUS_NOTIFICATION_ID,
                mConnectivityStatusNotificationController.getNotification(Unknown));
    }


    private void initDependencies() {
        mConnectivityStatusNotificationController = MyMobileApplication
                .getApplicationComponent()
                .getConnectivityStatusNotificationController();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this.getApplicationContext(), this, this))
                .build()
                .googleApiClient();
    }

    @Override
    public void onDestroy() {
        unRegisterCapabilityChangedCallbacks();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("google API client connected, retrieving nodes");
        registerCapabilityChangedCallbacks();
        getNodes();
    }

    private void registerCapabilityChangedCallbacks() {
        Wearable.CapabilityApi
                .addCapabilityListener(mGoogleApiClient, this, WATCH_CAPABILITY);
    }

    private void unRegisterCapabilityChangedCallbacks() {
        Wearable.CapabilityApi
                .removeCapabilityListener(mGoogleApiClient, this, WATCH_CAPABILITY);
    }

    private void getNodes() {

        Timber.d("retrieving nodes through Capability Api");

        Wearable.CapabilityApi
                .getCapability(mGoogleApiClient, WATCH_CAPABILITY, FILTER_REACHABLE)
                .setResultCallback(new ResultCallbacks<CapabilityApi.GetCapabilityResult>() {

                    @Override
                    public void onSuccess(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {

                        Set<Node> nodes = getCapabilityResult.getCapability().getNodes();
                        Timber.d("nodes amount from Capability Api: %s", nodes.size());

                        Node directlyConnectedNode = null;
                        for (Node node : nodes) {
                            if (node.isNearby()) {
                                directlyConnectedNode = node;
                            }
                        }

                        if (directlyConnectedNode == null) {
                            mConnectivityStatusNotificationController.sendNotification(NotConnectedToWatch);
                        } else {
                            mConnectivityStatusNotificationController.sendNotification(ConnectedToWatch);
                        }
                    }


                    @Override
                    public void onFailure(@NonNull Status status) {
                        Timber.d("failed to retrieve the capabilities, the status: %s", status.getStatusMessage());
                        mConnectivityStatusNotificationController.sendNotification(NotConnectedToWatch);
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
        mConnectivityStatusNotificationController.sendNotification(NotConnectedToWatch);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
        mConnectivityStatusNotificationController.sendNotification(NotConnectedToWatch);
    }


    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Timber.d("capability changed, the nodes: %s",
                capabilityInfo.getNodes());

        Node directlyConnectedNode = null;
        for (Node node : capabilityInfo.getNodes()) {
            if (node.isNearby()) {
                Timber.d("Capability of directly connected node changed, node " +
                        "info: %s", node.getDisplayName());
                directlyConnectedNode = node;
            }
        }

        if (directlyConnectedNode == null) {
            mConnectivityStatusNotificationController.sendNotification(NotConnectedToWatch);
        } else {
            mConnectivityStatusNotificationController.sendNotification(ConnectedToWatch);
        }
    }
}
