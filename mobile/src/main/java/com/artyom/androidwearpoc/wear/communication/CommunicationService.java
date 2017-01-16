package com.artyom.androidwearpoc.wear.communication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.shared.models.UpdateChunkLimitMessage;
import com.artyom.androidwearpoc.shared.models.UpdateSamplingRateMessage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.UPDATE_SAMPLES_PER_PACKAGE_PATH;
import static com.artyom.androidwearpoc.shared.CommonConstants.UPDATE_SAMPLING_RATE_PATH;
import static com.artyom.androidwearpoc.shared.CommonConstants.WATCH_CAPABILITY;
import static com.artyom.androidwearpoc.wear.communication.CommunicationController.ACTION;
import static com.artyom.androidwearpoc.wear.communication.CommunicationController.AMOUNT;
import static com.artyom.androidwearpoc.wear.communication.CommunicationController.RATE;
import static com.artyom.androidwearpoc.wear.communication.CommunicationController.UPDATE_RATE_ACTION;
import static com.artyom.androidwearpoc.wear.communication.CommunicationController.UPDATE_SAMPLES_PER_CHUNK_ACTION;

/**
 * Created by Artyom-IDEO on 12-Jan-17.
 */

public class CommunicationService extends IntentService
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private EventBus mEventBus;

    public CommunicationService() {
        super("CommunicationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this, this, this))
                .build()
                .googleApiClient();

        mEventBus = MyMobileApplication.getApplicationComponent()
                .getEventBus();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras() != null
                && validateConnection()) {
            String action = intent.getStringExtra(ACTION);
            switch (action) {
                case UPDATE_RATE_ACTION:
                    int newRate = intent.getIntExtra(RATE, -1);
                    if (newRate != -1) {
                        sendUpdateRateMessageToWearable(newRate);
                    }
                    break;
                case UPDATE_SAMPLES_PER_CHUNK_ACTION:
                    int newLimit = intent.getIntExtra(AMOUNT, -1);
                    if (newLimit != -1) {
                        sendChunkLimitMessageToWearable(newLimit);
                    }
                    break;
            }
        } else {
            Timber.e("google client failed to connect");
        }
    }

    private void sendChunkLimitMessageToWearable(int newLimit) {
        CapabilityApi.GetCapabilityResult result =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient, WATCH_CAPABILITY,
                        CapabilityApi.FILTER_REACHABLE).await();

        Set<Node> nodes = result.getCapability().getNodes();
        Timber.d("nodes amount from Capability Api: %s", nodes.size());

        Node directlyConnectedNode = null;
        for (Node node : nodes) {
            if (node.isNearby()) {
                directlyConnectedNode = node;
            }
        }

        if (directlyConnectedNode == null) {
            mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent("Failed to update " +
                    "samples per chunk amount"));
        } else {

            UpdateChunkLimitMessage message = new UpdateChunkLimitMessage(newLimit);
            byte[] serializedMessage = ParcelableUtil.marshall(message);

            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                    directlyConnectedNode.getId(),
                    UPDATE_SAMPLES_PER_PACKAGE_PATH,
                    serializedMessage).setResultCallback(new ResultCallbacks<MessageApi.SendMessageResult>() {

                @Override
                public void onSuccess(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                    mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent
                            ("Successfully sent a message to watch"));
                }

                @Override
                public void onFailure(@NonNull Status status) {
                    mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent("Failed to update " +
                            "samples per package amount"));
                }
            });
        }
    }

    private void sendUpdateRateMessageToWearable(int newRate) {

        CapabilityApi.GetCapabilityResult result =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient, WATCH_CAPABILITY,
                        CapabilityApi.FILTER_REACHABLE).await();

        Set<Node> nodes = result.getCapability().getNodes();
        Timber.d("nodes amount from Capability Api: %s", nodes.size());

        Node directlyConnectedNode = null;
        for (Node node : nodes) {
            if (node.isNearby()) {
                directlyConnectedNode = node;
            }
        }

        if (directlyConnectedNode == null) {
            mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent("Failed to update " +
                    "sampling rate"));
        } else {

            UpdateSamplingRateMessage message = new UpdateSamplingRateMessage(newRate);
            byte[] serializedMessage = ParcelableUtil.marshall(message);

            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                    directlyConnectedNode.getId(),
                    UPDATE_SAMPLING_RATE_PATH,
                    serializedMessage).setResultCallback(new ResultCallbacks<MessageApi.SendMessageResult>() {

                @Override
                public void onSuccess(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                    mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent
                            ("Successfully sent a message to watch"));
                }

                @Override
                public void onFailure(@NonNull Status status) {
                    mEventBus.post(new com.artyom.androidwearpoc.events.MessageEvent("Failed to update " +
                            "sampling rate"));
                }
            });
        }
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
        Timber.d("google client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.w("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection error, cause: %s", connectionResult.getErrorMessage());
    }
}
