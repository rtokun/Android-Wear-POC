package com.artyom.androidwearpoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.artyom.androidwearpoc.BuildConfig;
import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.models.MeasurementServiceStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import static com.artyom.androidwearpoc.shared.Configuration.DEFAULT_SAMPLING_RATE_IN_MICRO;

public class MainActivity extends Activity implements View.OnClickListener {

    @Inject
    EventBus mEventBus;

    @Inject
    MeasurementServiceController mMeasurementServiceController;

    private ImageButton mImgBtnControlService;

    private TextView mTVTitle;

    private TextView mTVVersionCode;

    private TextView mTVRate;

    private ProgressBar mPBLoading;

    private FrameLayout mContentLayout;

    private boolean isMeasurementServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyWearApplication.getApplicationComponent().inject(this);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLoadingUI(true);
        mEventBus.register(this);
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

    @Override
    protected void onStop() {
        mEventBus.unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void measurementServiceStatusUpdated(MeasurementServiceStatus status) {
        isMeasurementServiceRunning = status.isRunning();
        updateUI();
    }

    private void updateUI() {
        if (isMeasurementServiceRunning) {
            mTVTitle.setText("Measurement status:\nRecording");
            mImgBtnControlService.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {
            mTVTitle.setText("Measurement status:\nNot recording");
            mImgBtnControlService.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
        mTVVersionCode.setText("Version code: " + BuildConfig.VERSION_CODE);
        mTVRate.setText(String.format("Sampling rate: %d Hz", 1000000 /
                DEFAULT_SAMPLING_RATE_IN_MICRO));
        setLoadingUI(false);
    }

    private void setListeners() {
        mImgBtnControlService.setOnClickListener(this);
    }

    private void findViews() {
        mImgBtnControlService = (ImageButton) findViewById(R.id.img_view_control_service);
        mTVTitle = (TextView) findViewById(R.id.tv_title);
        mTVRate = (TextView) findViewById(R.id.tv_rate);
        mTVVersionCode = (TextView) findViewById(R.id.tv_version_code);
        mContentLayout = (FrameLayout) findViewById(R.id.fr_lay_content);
        mPBLoading = (ProgressBar) findViewById(R.id.pb_loading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_view_control_service:
                toggleMeasurementService(isMeasurementServiceRunning);
                showConfirmationActivity(isMeasurementServiceRunning);
                break;
        }
    }

    private void showConfirmationActivity(boolean isMeasurementServiceRunning) {

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);

        String confirmationString = isMeasurementServiceRunning ? "Stop measure" : "Start measure";

        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, confirmationString);

        startActivity(intent);
    }

    private void toggleMeasurementService(boolean isMeasurementServiceRunning) {
        if (isMeasurementServiceRunning) {
            mMeasurementServiceController.stopMeasurementService();
        } else {
            mMeasurementServiceController.startMeasurementService();
        }
    }
}
