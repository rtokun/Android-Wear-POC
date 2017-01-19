package com.artyom.androidwearpoc.connectivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.WearGoogleApiModule;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.models.ConnectivityStatus;
import com.artyom.androidwearpoc.shared.models.UpdateChunkLimitMessage;
import com.artyom.androidwearpoc.shared.models.UpdateSamplingRateMessage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;
import com.artyom.androidwearpoc.util.WearConfigController;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.RESET_MEASUREMENT_PATH;
import static com.artyom.androidwearpoc.shared.CommonConstants.START_MEASUREMENT_PATH;
import static com.artyom.androidwearpoc.shared.CommonConstants.UPDATE_SAMPLES_PER_PACKAGE_PATH;
import static com.artyom.androidwearpoc.shared.CommonConstants.UPDATE_SAMPLING_RATE_PATH;

/**
 * Created by Artyom on 27/12/2016.
 */

public class ConnectivityService extends WearableListenerService
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String
            POC_MOBILE_CAPABILITY = "fox_mobile_capability";

    private GoogleApiClient mGoogleApiClient;

    private WearConfigController mConfigController;

    private MeasurementServiceController mMeasurementServiceController;

    private EventBus mEventBus;


    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent.builder()
                .wearGoogleApiModule(new WearGoogleApiModule(this, this, this))
                .build()
                .googleApiClient();

        mConfigController = ((MyWearApplication) getApplication()).getApplicationComponent()
                .getWearConfigController();

        mMeasurementServiceController = ((MyWearApplication) getApplication()).getApplicationComponent()
                .getMeasurementServiceController();

        mEventBus = ((MyWearApplication) getApplication()).getApplicationComponent()
                .getEventBus();
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
        switch (messageEvent.getPath()) {
            case UPDATE_SAMPLING_RATE_PATH:
                int newSamplingRate = getRate(messageEvent.getData());
                mConfigController.updateSamplingRate(newSamplingRate);
                break;
            case UPDATE_SAMPLES_PER_PACKAGE_PATH:
                int newLimit = getLimit(messageEvent.getData());
                mConfigController.updateUpdateSamplesPerChunk(newLimit);
                break;
            case START_MEASUREMENT_PATH:
                mMeasurementServiceController.startMeasurementService();
                break;
            case RESET_MEASUREMENT_PATH:
                mMeasurementServiceController.resetMeasurementService();
                break;
        }
    }


    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        if (capabilityInfo.getName().equals(POC_MOBILE_CAPABILITY)) {
            Node directlyConnectedNode = null;
            for (Node node : capabilityInfo.getNodes()) {
                if (node.isNearby()) {
                    directlyConnectedNode = node;
                }
            }

            if (directlyConnectedNode == null) {
                Timber.i("not connected to mobile");
                mEventBus.postSticky(new ConnectivityStatus(false));
            } else {
                Timber.i("Connected to mobile: %s", directlyConnectedNode.getDisplayName());
                mEventBus.postSticky(new ConnectivityStatus(true));
            }
        }
    }

    private int getLimit(byte[] data) {
        UpdateChunkLimitMessage message = ParcelableUtil.unmarshall(data, UpdateChunkLimitMessage.CREATOR);
        return message.getSamplesPerChunk();
    }

    private int getRate(byte[] data) {
        UpdateSamplingRateMessage message = ParcelableUtil.unmarshall(data, UpdateSamplingRateMessage.CREATOR);
        return message.getSamplingRate();
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
