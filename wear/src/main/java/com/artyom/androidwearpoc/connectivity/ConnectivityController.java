package com.artyom.androidwearpoc.connectivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import android.content.Context;
import android.os.AsyncTask;

import com.artyom.androidwearpoc.shared.CommonConstants;
import com.artyom.androidwearpoc.shared.models.ConnectivityStatus;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Artyom on 19/01/2017.
 */

public class ConnectivityController {

    private Context mAppContext;

    private EventBus mEventBus;

    public static final int CLIENT_CONNECTION_TIMEOUT = 15;

    public ConnectivityController(Context appContext, EventBus eventBus) {
        this.mAppContext = appContext;
        this.mEventBus = eventBus;
    }

    public void checkMobileConnection() {
        CheckMobileConnectionTask checkMobileConnectionTask = new CheckMobileConnectionTask();
        checkMobileConnectionTask.execute();
    }

    private class CheckMobileConnectionTask extends AsyncTask<Void, Void, Node> {

        private GoogleApiClient googleApiClient;

        @Override
        protected Node doInBackground(Void... params) {

            boolean success = createClient();
            if (!success){
                return null;
            }

            Node directlyConnectedNode = checkNearCapabilityNode();

            if (directlyConnectedNode != null){
                Timber.d("connected directly to matching mobile device");
            } else {
                Timber.w("not connected to matching mobile device");
            }

            return directlyConnectedNode;
        }

        @Override
        protected void onPostExecute(Node node) {
            if (node != null){
                mEventBus.postSticky(new ConnectivityStatus(true));
            } else {
                mEventBus.postSticky(new ConnectivityStatus(false));
            }
        }

        private Node checkNearCapabilityNode() {

            CapabilityApi.GetCapabilityResult result =
                    Wearable.CapabilityApi.getCapability(
                            googleApiClient,
                            CommonConstants.MOBILE_CAPABILITY,
                            CapabilityApi.FILTER_REACHABLE)
                            .await(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

            Node directlyConnectedNode = null;
            for (Node node: result.getCapability().getNodes()){
                if (node.isNearby()){
                    directlyConnectedNode = node;
                }
            }

            return directlyConnectedNode;
        }

        private boolean createClient() {

            googleApiClient = new GoogleApiClient.Builder(mAppContext)
                    .addApi(Wearable.API)
                    .build();

            ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

            if (!result.isSuccess()){
                Timber.e("failed to connect google client, the reason: %s", result.getErrorMessage());
                return false;
            } else {
                return true;
            }
        }
    }

}
