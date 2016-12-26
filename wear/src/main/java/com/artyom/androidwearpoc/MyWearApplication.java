package com.artyom.androidwearpoc;

import android.app.Application;
import android.os.Handler;

import com.artyom.androidwearpoc.dagger.components.DaggerWearApplicationComponent;
import com.artyom.androidwearpoc.dagger.components.WearApplicationComponent;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.dagger.modules.ApplicationContextModule;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MyWearApplication extends Application {

    @Inject
    MeasurementServiceController mMeasurementServiceController;

    private static WearApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        createDaggerApplicationController();
        startAppComponents();
    }

    private void createDaggerApplicationController() {
        mApplicationComponent = DaggerWearApplicationComponent
                .builder()
                .applicationContextModule(new ApplicationContextModule(this))
                .build();

        mApplicationComponent.inject(this);
    }

    private void startAppComponents() {
        Timber.d("starting wear app components in 10 seconds delay");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mMeasurementServiceController.startMeasurementService();
            }
        }, 10000);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static WearApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
