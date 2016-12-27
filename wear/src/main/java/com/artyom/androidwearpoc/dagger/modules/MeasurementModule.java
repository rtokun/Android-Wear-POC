package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;
import android.hardware.SensorManager;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;

import dagger.Module;
import dagger.Provides;


@Module(includes = ApplicationContextModule.class)
public class MeasurementModule {

    @ForApplication
    @Provides
    MeasurementServiceController measurementServiceController(Context applicationContext) {
        return new MeasurementServiceController(applicationContext);
    }

    @ForApplication
    @Provides
    SensorManager sensorManager(Context applicationContext) {
        return (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
    }


}
