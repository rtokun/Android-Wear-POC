package com.artyom.androidwearpoc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.artyom.androidwearpoc.BuildConfig;
import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.base.BaseEventActivity;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.models.ConnectivityStatus;
import com.artyom.androidwearpoc.util.WearConfigController;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends BaseEventActivity {

    @Inject
    MeasurementServiceController mMeasurementServiceController;

    @Inject
    WearConfigController mConfigController;

//    private ProgressBar mPBLoading;

    private FrameLayout mContentLayout;

    private boolean isConnected = false;

    private TextView mTVAppVersion;

    private TextView mTVConnectivityStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyWearApplication) getApplication()).getApplicationComponent().inject(this);
        setContentView(R.layout.activity_main_new);
        findViews();
//        updateAppVersion();
//        setLoadingUI(true);
    }

    private void updateAppVersion() {
        mTVAppVersion.setText("Application version: " + BuildConfig.VERSION_CODE);
    }

    private void setLoadingUI(boolean loadingVisible) {
        if (loadingVisible) {
            mContentLayout.setVisibility(View.GONE);
//            mPBLoading.setVisibility(View.VISIBLE);
        } else {
            mContentLayout.setVisibility(View.VISIBLE);
//            mPBLoading.setVisibility(View.GONE);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void mobileConnectivityStatusUpdated(ConnectivityStatus status) {
        isConnected = status.isConnected();
        updateUI();
    }

    private void updateUI() {
        if (isConnected) {
            mTVConnectivityStatus.setText("Connected");
            mTVConnectivityStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.connected, 0);
        } else {
            mTVConnectivityStatus.setText("Disconnected");
            mTVConnectivityStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.disconnected, 0);
        }
        setLoadingUI(false);
    }

    private void findViews() {
        mTVAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mTVConnectivityStatus = (TextView) findViewById(R.id.tv_connectivity_status);
        mContentLayout = (FrameLayout) findViewById(R.id.fr_lay_content);
//        mPBLoading = (ProgressBar) findViewById(R.id.pb_loading);
    }
}
