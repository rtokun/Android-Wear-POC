package com.artyom.androidwearpoc.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.artyom.androidwearpoc.BuildConfig;
import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.base.BaseEventActivity;
import com.artyom.androidwearpoc.connectivity.ConnectivityController;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.models.ConnectivityStatus;
import com.artyom.androidwearpoc.util.WearConfigController;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends BaseEventActivity {

    private ConnectivityController mConnectivityController;

    private ProgressBar mPBLoading;

    private LinearLayout mContentLayout;

    private Boolean isConnected;

    private TextView mTVAppVersion;

    private TextView mTVConnectivityStatus;

    private ImageView mImgConnectivityStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectivityController = ((MyWearApplication) getApplication())
                .getApplicationComponent()
                .getConnectivityController();
        setContentView(R.layout.activity_main_new);
        findViews();
        updateAppVersion();
        setLoadingUI(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected == null){
            mConnectivityController.checkMobileConnection();
        }
    }

    private void updateAppVersion() {
        mTVAppVersion.setText("Application version: "
                + BuildConfig.VERSION_NAME
                + "." +
                BuildConfig.VERSION_CODE);
    }

    private void setLoadingUI(boolean loadingVisible) {
        if (loadingVisible) {
            mContentLayout.setVisibility(View.GONE);
            mPBLoading.setVisibility(View.VISIBLE);
        } else {
            mContentLayout.setVisibility(View.VISIBLE);
            mPBLoading.setVisibility(View.GONE);
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
            mImgConnectivityStatus.setImageResource(R.drawable.connected);
        } else {
            mTVConnectivityStatus.setText("Disconnected");
            mImgConnectivityStatus.setImageResource(R.drawable.disconnected);
        }
        setLoadingUI(false);
    }

    private void findViews() {
        mTVAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mTVConnectivityStatus = (TextView) findViewById(R.id.tv_connectivity_status);
        mImgConnectivityStatus = (ImageView) findViewById(R.id.img_connectivity_status);
        mContentLayout = (LinearLayout) findViewById(R.id.lin_lay_content);
        mPBLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mPBLoading.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE,
                PorterDuff.Mode.MULTIPLY);
    }
}
