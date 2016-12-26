package com.artyom.androidwearpoc;

import android.app.Application;

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
        mMeasurementServiceController.startMeasurementService();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static WearApplicationComponent getmApplicationComponent() {
        return mApplicationComponent;
    }
}
