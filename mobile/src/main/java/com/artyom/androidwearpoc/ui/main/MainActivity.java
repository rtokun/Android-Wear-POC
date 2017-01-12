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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artyom.androidwearpoc.BuildConfig;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.dagger.components.DBReposComponent;
import com.artyom.androidwearpoc.dagger.components.DaggerDBReposComponent;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.BatteryLevelSamplesRepo;
import com.artyom.androidwearpoc.export.CSVExportTask;
import com.artyom.androidwearpoc.ui.ExportFileDialog;
import com.artyom.androidwearpoc.ui.utils.Conversions;
import com.artyom.androidwearpoc.util.SharedPrefsController;

import java.io.File;
import java.util.List;

import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String WATCH_CAPABILITY = "fox_watch_capability";

    public static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1001;

    GoogleApiClient mGoogleApiClient;

    private ProgressBar mProgressbar;

    private Button mBtnCSVExport;

    private Button mBtnDeleteData;

    private Button mBtnCountSamples;

    private Button mBtnSubmitSamplingRate;

    private TextView mTvVersion;

    private TextInputEditText mEDSamplingRate;

    private CoordinatorLayout mMainCoordinatorLayout;

    private AccelerometerSamplesRepo mAccelerometerSamplesRepo;

    private BatteryLevelSamplesRepo mBatteryLevelSamplesRepo;

    private SharedPrefsController mSharedPrefsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBReposComponent dbReposComponent = DaggerDBReposComponent
                .builder()
                .build();

        mAccelerometerSamplesRepo = dbReposComponent.getAccelerometerSamplesRepo();
        mBatteryLevelSamplesRepo = dbReposComponent.getBatteryLevelSamplesRepo();

        findViews();
        setListeners();
        initUI();
        createGoogleClient();
        addCapabilityListener();
        requestRequiredPermissions();
    }

    private void initUI() {
        mTvVersion.setText("Version: " + BuildConfig.VERSION_NAME);
        mEDSamplingRate.setText("50");

    }

    private void setListeners() {
        mBtnCSVExport.setOnClickListener(this);
        mBtnDeleteData.setOnClickListener(this);
        mBtnCountSamples.setOnClickListener(this);
        mBtnSubmitSamplingRate.setOnClickListener(this);
    }

    private void findViews() {
        mProgressbar = (ProgressBar) findViewById(R.id.progressBarExport);
        mBtnCSVExport = (Button) findViewById(R.id.buttonCSV);
        mBtnDeleteData = (Button) findViewById(R.id.buttonDeleteAllData);
        mBtnCountSamples = (Button) findViewById(R.id.buttonCountSamples);
        mMainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mEDSamplingRate = (TextInputEditText) findViewById(R.id.ed_rate);
        mBtnSubmitSamplingRate = (Button) findViewById(R.id.btn_submit_rate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectGoogleClient();
    }

    private boolean requestRequiredPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Timber.i("permission granted: write external storage");

                } else {

                    showDeniedPermissionSnackBar();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private void showDeniedPermissionSnackBar() {

        Snackbar permissionDeniedSnackbar = Snackbar
                .make(mMainCoordinatorLayout, "Permission denied", Snackbar.LENGTH_LONG)
                .setAction("GRANT", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        requestRequiredPermissions();
                    }
                });

        permissionDeniedSnackbar.show();
    }

    @Override
    protected void onPause() {
        disconnectGoogleClient();
        super.onPause();
    }

    private void disconnectGoogleClient() {
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
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

    private void exportCSV() {
        CSVExportTask exportTask = new CSVExportTask(mProgressbar, new CSVExportTask.Callback() {

            @Override
            public void onSuccess(File exportFile) {
                Timber.i("Loading email dialog");
                showCSVFileExportDialog(exportFile.getAbsolutePath(), true,
                        "Export file saved to device: " + exportFile.getAbsolutePath() +
                                " \nSize in MB:" + Conversions.humanReadableByteCount(exportFile.length(), true) +
                                "\nWant to share?");
            }

            @Override
            public void onFailure(String message) {
                Timber.e("CSV export failed %s", message);
                showCSVFileExportDialog("", false, "Failed to export file: \n" + message);
            }

            @Override
            public void onNoData() {
                Timber.w("CSV export failed - no data");
                showCSVFileExportDialog("", false, "No data to export");
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

    public void deleteAllData() {
        mAccelerometerSamplesRepo.deleteAll();
        mBatteryLevelSamplesRepo.deleteAll();
        Toast.makeText(this, "All data has been deleted", Toast.LENGTH_LONG).show();
    }

    public void countSamples() {
        long count = mAccelerometerSamplesRepo.count();
        Toast.makeText(this, "There are: " + count + " accelerometer samples", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCSV:
                exportCSV();
                break;
            case R.id.buttonDeleteAllData:
                deleteAllData();
                break;
            case R.id.buttonCountSamples:
                countSamples();
                break;
            case R.id.btn_submit_rate:
                mEDSamplingRate.clearFocus();
                int newRate = Integer.valueOf(mEDSamplingRate.getText().toString());
                if (isRateChanged()){
                    sendNewRateToWearable();
                }
                break;
        }
    }

    public void isRateChanged() {

    }
}
