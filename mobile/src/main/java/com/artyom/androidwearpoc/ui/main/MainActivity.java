package com.artyom.androidwearpoc.ui.main;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artyom.androidwearpoc.BuildConfig;
import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.base.BaseEventActivity;
import com.artyom.androidwearpoc.dagger.components.DBReposComponent;
import com.artyom.androidwearpoc.dagger.components.DaggerDBReposComponent;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.BatteryLevelSamplesRepo;
import com.artyom.androidwearpoc.events.MessageEvent;
import com.artyom.androidwearpoc.export.CSVExportTask;
import com.artyom.androidwearpoc.export.EmailSender;
import com.artyom.androidwearpoc.ui.ExportFileDialog;
import com.artyom.androidwearpoc.ui.ExportFileDialog.ExportFileDialogInteractionInterface;
import com.artyom.androidwearpoc.ui.utils.Conversions;
import com.artyom.androidwearpoc.util.ConfigController;
import com.bytesizebit.androidutils.KeyboardUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends BaseEventActivity implements
        View.OnClickListener,
        ExportFileDialogInteractionInterface {

    public static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1001;

    private ProgressBar mProgressbar;

    private Button mBtnCSVExport;

    private Button mBtnDeleteData;

    private Button mBtnCountSamples;

    private TextView mTvVersion;

    private TextInputEditText mEDSamplingRate;

    private TextInputEditText mEDSamplesPerChunk;

    private CoordinatorLayout mMainCoordinatorLayout;

    private AccelerometerSamplesRepo mAccelerometerSamplesRepo;

    private BatteryLevelSamplesRepo mBatteryLevelSamplesRepo;

    private ConfigController mConfigController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDependencies();
        findViews();
        setListeners();
        initUI();
        requestRequiredPermissions();
    }

    @Subscribe
    public void onWearMessageEvent(MessageEvent messageEvent) {
        Toast.makeText(this, messageEvent.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void initDependencies() {
        DBReposComponent dbReposComponent = DaggerDBReposComponent
                .builder()
                .build();

        mAccelerometerSamplesRepo = dbReposComponent.getAccelerometerSamplesRepo();
        mBatteryLevelSamplesRepo = dbReposComponent.getBatteryLevelSamplesRepo();

        mConfigController = MyMobileApplication.getApplicationComponent()
                .getConfigController();
    }

    private void initUI() {
        mTvVersion.setText("Version: " + BuildConfig.VERSION_NAME);

        int samplingRate = mConfigController.getSamplingRate();
        mEDSamplingRate.setText(String.valueOf(samplingRate));

        int samplesPerChunk = mConfigController.getSamplesPerChunk();
        mEDSamplesPerChunk.setText(String.valueOf(samplesPerChunk));
    }

    private void setListeners() {
        mBtnCSVExport.setOnClickListener(this);
        mBtnDeleteData.setOnClickListener(this);
        mBtnCountSamples.setOnClickListener(this);
        mEDSamplingRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftKeyboard(MainActivity.this, mEDSamplingRate);
                    int newRate = Integer.valueOf(mEDSamplingRate.getText().toString());
                    if (isRateChanged(newRate)) {
                        mConfigController.updateSamplingRate(newRate);
                        Toast.makeText(MainActivity.this, "Updating sampling rate", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                return false;
            }
        });
        mEDSamplesPerChunk.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftKeyboard(MainActivity.this, mEDSamplesPerChunk);
                    int newLimit = Integer.valueOf(mEDSamplesPerChunk.getText().toString());
                    if (isLimitChanged(newLimit)) {
                        mConfigController.updateSamplesPerChunk(newLimit);
                        Toast.makeText(MainActivity.this, "Updating samples per chunk limit", Toast
                                .LENGTH_SHORT)
                                .show();
                    }
                }
                return false;
            }
        });
    }

    private boolean isLimitChanged(int newLimit) {
        int previous = mConfigController.getSamplesPerChunk();
        return newLimit != previous;
    }

    private void findViews() {
        mProgressbar = (ProgressBar) findViewById(R.id.progressBarExport);
        mBtnCSVExport = (Button) findViewById(R.id.buttonCSV);
        mBtnDeleteData = (Button) findViewById(R.id.buttonDeleteAllData);
        mBtnCountSamples = (Button) findViewById(R.id.buttonCountSamples);
        mMainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mEDSamplingRate = (TextInputEditText) findViewById(R.id.ed_rate);
        mEDSamplesPerChunk = (TextInputEditText) findViewById(R.id.ed_samples_limit);
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

    private void exportCSV() {
        CSVExportTask exportTask = new CSVExportTask(mProgressbar, new CSVExportTask.Callback() {

            @Override
            public void onSuccess(File exportFile) {
                Timber.i("Loading email dialog");
                showCSVFileExportDialog(exportFile.getAbsolutePath(),
                        true,
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
        }
    }

    public boolean isRateChanged(int newRate) {
        int savedRateInHz = mConfigController.getSamplingRate();
        return newRate != savedRateInHz;
    }

    @Override
    public void onShareClick(String pathToFile) {
        EmailSender.sendFileInEmail(this, new File(pathToFile));
    }
}
