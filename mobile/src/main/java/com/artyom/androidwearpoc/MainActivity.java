package com.artyom.androidwearpoc;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.artyom.androidwearpoc.export.CSVExportTask;
import com.artyom.androidwearpoc.model.AccelerometerSample;
import com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusNotificationController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static com.google.android.gms.wearable.CapabilityApi.FILTER_REACHABLE;
import static com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String WATCH_CAPABILITY = "fox_watch_capability";

    GoogleApiClient mGoogleApiClient;

    private ProgressBar mProgressbar;

    @Inject
    ConnectivityStatusNotificationController mConnectivityStatusNotificationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyMobileApplication.getApplicationComponent().inject(this);
        setContentView(R.layout.activity_main);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBarExport);
        createGoogleClient();
        addCapabilityListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectGoogleClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void connectGoogleClient() {
        mGoogleApiClient.connect();
    }

    private void addCapabilityListener() {
        Timber.d("adding capability changed listener");
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                new CapabilityApi.CapabilityListener() {

                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                        Timber.d("capability changed, the nodes: %s",
                                capabilityInfo.getNodes());

                        for (Node node : capabilityInfo.getNodes()) {
                            if (node.isNearby()) {
                                Timber.d("Capability of directly connected node changed, node " +
                                        "info: %s", node.getDisplayName());
                                return;
                            }
                        }
                    }
                },
                WATCH_CAPABILITY);
    }

    private void createGoogleClient() {
        Timber.d("creating google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("google API client connected, retrieving nodes");
//        getNodesNodeApi();
        getNodesCapabilityApi();
    }

    private void getNodesNodeApi() {
        Timber.d("retrieving nodes through Node Api");
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {

                    @Override
                    public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        List<Node> nodes = getConnectedNodesResult.getNodes();
                        Timber.d("nodes amount from Node Api: %s", nodes.size());

                        if (nodes.size() > 0) {
                            for (int i = 0; i < nodes.size(); i++) {
                                Timber.d("node #%s is: %s", i, nodes.get(i).getDisplayName());
                            }
                        }
                    }
                });
    }

    private void getNodesCapabilityApi() {
        Timber.d("retrieving nodes through Capability Api");
        Wearable.CapabilityApi
                .getCapability(mGoogleApiClient, WATCH_CAPABILITY, FILTER_REACHABLE)
                .setResultCallback(new ResultCallbacks<GetCapabilityResult>() {

                    @Override
                    public void onSuccess(@NonNull GetCapabilityResult getCapabilityResult) {

                        Set<Node> nodes = getCapabilityResult.getCapability().getNodes();
                        Timber.d("nodes amount from Capability Api: %s", nodes.size());

                        if (nodes.size() > 0) {
                            for (Node node : nodes) {
                                Timber.d("node is: %s", node.getDisplayName());
                            }
                        }
                    }


                    @Override
                    public void onFailure(@NonNull Status status) {
                        Timber.d("failed to retrieve the capabilities, the status: %s", status.getStatusMessage());
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }


    public void onCSVButtonClick(View view) {
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        generateRandomAccelerometerSamples(realm);
        realm.close();
        CSVExportTask exportTask = new CSVExportTask(true, mProgressbar);
        exportTask.execute();
    }



    private void generateRandomAccelerometerSamples(Realm realm){

        Realm.Transaction transaction = new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                long ts = System.currentTimeMillis();
                for(int i=0; i< 300; i++) {
                    AccelerometerSample sample = realm.createObject(AccelerometerSample.class);
                    sample.setTs(ts + i * 20);
                    sample.setX(i);
                    sample.setY(i);
                    sample.setZ(i);
                }
            }
        };
        realm.executeTransactionAsync(transaction, new Realm.Transaction.OnSuccess(){

            @Override
            public void onSuccess() {
                Timber.i("Random acc samples saved to db");
            }
        }, new Realm.Transaction.OnError(){

            @Override
            public void onError(Throwable error) {
                Timber.i("Random acc samples saving to db failed %s", error.getMessage());
            }
        });
    }





}
