package com.artyom.androidwearpoc;

import android.app.Application;

import com.artyom.androidwearpoc.dagger.components.DaggerMobileApplicationComponent;
import com.artyom.androidwearpoc.dagger.components.MobileApplicationComponent;
import com.artyom.androidwearpoc.dagger.modules.ApplicationContextModule;
import com.artyom.androidwearpoc.wear.connectivity.WearConnectivityServiceController;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by Artyom on 23/12/2016.
 */

public class MyMobileApplication extends Application {

    private static MobileApplicationComponent mMobileApplicationComponent;

    @Inject
    WearConnectivityServiceController mWearConnectivityServiceController;

    //region Accessors
    public static MobileApplicationComponent getApplicationComponent() {
        return mMobileApplicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        createDaggerApplicationController();
        startAppComponents();
        initCrashlytics();
    }

    private void startAppComponents() {
        mWearConnectivityServiceController.startWearConnectivityService();
        Realm.init(this);
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        Realm.getDefaultInstance().deleteAll();
//        realm.commitTransaction();
    }

    private void initCrashlytics() {
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();

        Fabric.with(fabric);
    }

    private void createDaggerApplicationController() {
        mMobileApplicationComponent = DaggerMobileApplicationComponent
                .builder()
                .applicationContextModule(new ApplicationContextModule(this))
                .build();

        mMobileApplicationComponent.inject(this);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    //endregion
}
