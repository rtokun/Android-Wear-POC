package com.artyom.androidwearpoc;

import android.app.Application;

import com.artyom.androidwearpoc.dagger.components.ApplicationComponent;
import com.artyom.androidwearpoc.dagger.components.DaggerApplicationComponent;
import com.artyom.androidwearpoc.shared.dagger.modules.ApplicationContextModule;
import com.artyom.androidwearpoc.wear.connectivity.WearConnectivityServiceController;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Artyom on 23/12/2016.
 */

public class MyMobileApplication extends Application {

    private static ApplicationComponent mApplicationComponent;

    @Inject
    WearConnectivityServiceController mWearConnectivityServiceController;

    //region Accessors
    public static ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        createDaggerApplicationController();
        startAppComponents();
    }

    private void startAppComponents() {
        mWearConnectivityServiceController.startWearConnectivityService();
    }

    private void createDaggerApplicationController() {
        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationContextModule(new ApplicationContextModule(this))
                .build();

        mApplicationComponent.inject(this);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    //endregion
}
