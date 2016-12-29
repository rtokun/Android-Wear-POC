package com.artyom.androidwearpoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.models.MeasurementServiceStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends Activity implements View.OnClickListener {

    @Inject
    EventBus mEventBus;

    @Inject
    MeasurementServiceController mMeasurementServiceController;

    private ImageButton mImgBtnControlService;

    private TextView mtv_Title;

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
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        mEventBus.unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void measurementServiceStatusUpdated(MeasurementServiceStatus status){
        isMeasurementServiceRunning = status.isRunning();
        updateUI();
    }

    private void updateUI() {
        if (isMeasurementServiceRunning) {
            mtv_Title.setText("Measurement status:\nRecording");
            mImgBtnControlService.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {
            mtv_Title.setText("Measurement status:\nNot recording");
            mImgBtnControlService.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
    }

    private void setListeners() {
        mImgBtnControlService.setOnClickListener(this);
    }

    private void findViews() {
        mImgBtnControlService = (ImageButton) findViewById(R.id.img_view_control_service);
        mtv_Title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,confirmationString);

        startActivity(intent);
    }

    private void toggleMeasurementService(boolean isMeasurementServiceRunning) {
        if (isMeasurementServiceRunning){
            mMeasurementServiceController.stopMeasurementService();
        } else {
            mMeasurementServiceController.startMeasurementService();
        }
    }
}
