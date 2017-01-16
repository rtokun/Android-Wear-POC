package com.artyom.androidwearpoc.connectivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.WearGoogleApiModule;
import com.artyom.androidwearpoc.shared.models.UpdateNumberMessage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;
import com.artyom.androidwearpoc.util.WearConfigController;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.UPDATE_SAMPLING_RATE_PATH;

/**
 * Created by Artyom on 27/12/2016.
 */

public class WearDataReceiverService extends WearableListenerService
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private GoogleApiClient mGoogleApiClient;

    private WearConfigController mConfigController;

    private String mLocalNodeId;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent.builder()
                .wearGoogleApiModule(new WearGoogleApiModule(this, this, this))
                .build()
                .googleApiClient();

        mConfigController = MyWearApplication.getApplicationComponent()
                .getWearConfigController();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        switch (messageEvent.getPath()){
            case UPDATE_SAMPLING_RATE_PATH:
                int newSamplingRate = getNewSamplingRate(messageEvent.getData());
                mConfigController.updateSamplingRate(newSamplingRate);
                break;
        }
    }

    private int getNewSamplingRate(byte[] data) {
        UpdateNumberMessage message = ParcelableUtil.unmarshall(data, UpdateNumberMessage.CREATOR);
        return message.getNewNumber();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("google client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }
}
