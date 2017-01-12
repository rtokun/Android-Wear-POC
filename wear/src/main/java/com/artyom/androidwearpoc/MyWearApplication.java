package com.artyom.androidwearpoc;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.artyom.androidwearpoc.dagger.components.DaggerWearApplicationComponent;
import com.artyom.androidwearpoc.dagger.components.WearApplicationComponent;
import com.artyom.androidwearpoc.dagger.modules.WearApplicationContextModule;
import com.artyom.androidwearpoc.error.ErrorProcessingService;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */

public class MyWearApplication extends Application {

    @Inject
    MeasurementServiceController mMeasurementServiceController;

    private static WearApplicationComponent mApplicationComponent;

    // Android Wear's default UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultUEH;

    @Override
    public void onCreate() {
        super.onCreate();
        initExceptionHandler();
        initTimber();
        createDaggerApplicationController();
        startAppComponents();
    }

    private void initExceptionHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mWearUEH);
    }

    private void createDaggerApplicationController() {
        mApplicationComponent = DaggerWearApplicationComponent
                .builder()
                .applicationContextModule(new WearApplicationContextModule(this))
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
        }, 5000);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static WearApplicationComponent getApplicationComponent() {
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
