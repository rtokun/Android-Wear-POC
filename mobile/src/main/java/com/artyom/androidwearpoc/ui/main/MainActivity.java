package com.artyom.androidwearpoc.ui.main;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.dagger.components.DBReposComponent;
import com.artyom.androidwearpoc.dagger.components.DaggerDBReposComponent;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.BatteryLevelSamplesRepo;
import com.artyom.androidwearpoc.export.CSVExportTask;
import com.artyom.androidwearpoc.ui.ExportFileDialog;
import com.artyom.androidwearpoc.ui.utils.Conversions;

import java.io.File;
import java.util.List;

import timber.log.Timber;



public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String WATCH_CAPABILITY = "fox_watch_capability";

    GoogleApiClient mGoogleApiClient;

    private ProgressBar mProgressbar;

    private AccelerometerSamplesRepo mAccelerometerSamplesRepo;

    private BatteryLevelSamplesRepo mBatteryLevelSamplesRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBReposComponent dbReposComponent = DaggerDBReposComponent
                .builder()
                .build();

        mAccelerometerSamplesRepo = dbReposComponent.getAccelerometerSamplesRepo();
        mBatteryLevelSamplesRepo = dbReposComponent.getBatteryLevelSamplesRepo();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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


    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }

    public void onCSVButtonClick(View view) {
        CSVExportTask exportTask = new CSVExportTask(mProgressbar, new CSVExportTask.Callback() {
            @Override
            public void onSuccess(File exportFile) {
                Timber.i("Loading email dialog");
                showCSVFileExportDialog(exportFile.getAbsolutePath(),true,
                        "Export file saved to device: " + exportFile.getAbsolutePath() +
                        " \nSize in MB:" + Conversions.humanReadableByteCount(exportFile.length(),true) +
                         "\nWant to share?");
            }

            @Override
            public void onFailure(String message) {
                Timber.e("CSV export failed %s", message);
                showCSVFileExportDialog("",false, "Failed to export file: \n" + message);
            }

            @Override
            public void onNoData() {
                Timber.w("CSV export failed - no data");
                showCSVFileExportDialog("",false, "No data to export");
            }
        });
        exportTask.execute();
    }

    void showCSVFileExportDialog(String accSamplesFilePath, boolean success, String text) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ExportFileDialog.newInstance(
                this,
                success,
                text,
                accSamplesFilePath);
        newFragment.show(ft, "dialog");
    }

    public void onDeleteAllClick(View view) {
        mAccelerometerSamplesRepo.deleteAll();
        mBatteryLevelSamplesRepo.deleteAll();
        Toast.makeText(this,"All data has been deleted",Toast.LENGTH_LONG).show();
    }

    public void onCountSamplesClick(View view) {
        long count = mAccelerometerSamplesRepo.count();
        Toast.makeText(this,"There are: " + count + " accelerometer samples",Toast.LENGTH_LONG)
                .show();
    }
}
