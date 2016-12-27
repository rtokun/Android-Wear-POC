package com.artyom.androidwearpoc.wear.connectivity;

import android.content.Context;
import android.content.Intent;


import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import javax.inject.Inject;

/**
 * Created by Artyom on 24/12/2016.
 */
@ForApplication
public class WearConnectivityServiceController {

    @Inject
    Context mApplicationContext;

    public WearConnectivityServiceController(Context applicationContext) {
        this.mApplicationContext = applicationContext;
    }

    public void startWearConnectivityService() {
        Intent measurementService = new Intent(mApplicationContext, WearConnectivityService.class);
        mApplicationContext.startService(measurementService);
    }

}
