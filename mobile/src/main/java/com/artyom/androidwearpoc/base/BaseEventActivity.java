package com.artyom.androidwearpoc.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.artyom.androidwearpoc.MyMobileApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Artyom on 12/01/2017.
 */

public class BaseEventActivity extends AppCompatActivity {

    private EventBus mEventBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = MyMobileApplication.getApplicationComponent()
                .getEventBus();
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
}
