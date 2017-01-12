package com.artyom.androidwearpoc.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyWearApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Artyom on 12/01/2017.
 */

public class BaseEventActivity extends Activity {

    private EventBus mEventBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = MyWearApplication.getApplicationComponent()
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
