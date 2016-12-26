package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;
import android.hardware.SensorManager;

import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.dagger.modules.ApplicationContextModule;
import com.artyom.androidwearpoc.shared.dagger.scopes.ForApplication;

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
