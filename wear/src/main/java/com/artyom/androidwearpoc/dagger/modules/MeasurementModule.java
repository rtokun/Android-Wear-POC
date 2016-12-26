package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.artyom.androidwearpoc.dagger.qualifiers.AccelerometerSensor;
import com.artyom.androidwearpoc.dagger.qualifiers.HeartRateSensor;
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

    @ForApplication
    @Provides
    @HeartRateSensor
    Sensor heartRateSensor(SensorManager sensorManager) {
        return sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @ForApplication
    @Provides
    @AccelerometerSensor
    Sensor accelerometerSensor(SensorManager sensorManager) {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


}
