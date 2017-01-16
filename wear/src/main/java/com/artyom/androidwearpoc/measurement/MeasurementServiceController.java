package com.artyom.androidwearpoc.measurement;

import android.content.Context;
import android.content.Intent;


import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import javax.inject.Inject;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */
@ForApplication
public class MeasurementServiceController {

    Context mApplicationContext;

    @Inject
    public MeasurementServiceController(Context applicationContext) {
        this.mApplicationContext = applicationContext;
    }

    public void startMeasurementService() {
        Intent measurementService = new Intent(mApplicationContext, MeasurementService.class);
        mApplicationContext.startService(measurementService);
    }


    public void stopMeasurementService() {
        Intent measurementService = new Intent(mApplicationContext, MeasurementService.class);
        mApplicationContext.stopService(measurementService);
    }

    public void resetMeasurementService() {
        stopMeasurementService();
        startMeasurementService();
    }
}
