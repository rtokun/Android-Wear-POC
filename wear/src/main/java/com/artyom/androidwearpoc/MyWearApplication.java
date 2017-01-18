package com.artyom.androidwearpoc;

import android.app.Application;
import android.content.Intent;

import com.artyom.androidwearpoc.dagger.components.DaggerWearApplicationComponent;
import com.artyom.androidwearpoc.dagger.components.WearApplicationComponent;
import com.artyom.androidwearpoc.dagger.modules.WearApplicationContextModule;
import com.artyom.androidwearpoc.error.ErrorProcessingService;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MyWearApplication extends Application {

    private WearApplicationComponent mApplicationComponent;

    // Android Wear's default UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultUEH;

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
        initTimber();
        startMeasurementService();
    }

    private void startMeasurementService() {
        getApplicationComponent()
                .getMeasurementServiceController()
                .startMeasurementService();
    }

    private void initExceptionHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mWearUEH);
    }

    private void createDaggerApplicationController() {
        mApplicationComponent = DaggerWearApplicationComponent
                .builder()
                .wearApplicationContextModule(new WearApplicationContextModule(this))
                .build();
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public WearApplicationComponent getApplicationComponent() {
        if (mApplicationComponent == null) {
            createDaggerApplicationController();
        }
        return mApplicationComponent;
    }

    private Thread.UncaughtExceptionHandler mWearUEH = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(final Thread thread, final Throwable ex) {

            // Pass the exception to a Service which will send the data upstream to your Smartphone/Tablet
            Intent errorIntent = new Intent(MyWearApplication.this, ErrorProcessingService.class);
            errorIntent.putExtra("exception", ex);
            startService(errorIntent);

            // Let the default UncaughtExceptionHandler take it from here
            mDefaultUEH.uncaughtException(thread, ex);
        }
    };
}
