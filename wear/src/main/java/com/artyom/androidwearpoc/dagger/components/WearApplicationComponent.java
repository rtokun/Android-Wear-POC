package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.modules.MeasurementModule;
import com.artyom.androidwearpoc.measurement.MeasurementService;
import com.artyom.androidwearpoc.measurement.MeasurementServiceController;
import com.artyom.androidwearpoc.shared.dagger.scopes.ForApplication;

import dagger.Component;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */
@ForApplication
@Component(modules = {MeasurementModule.class})
public interface WearApplicationComponent {

    MeasurementServiceController measurementServiceController();

    void inject(MyWearApplication myWearApplication);

    void inject(MeasurementService measurementService);

}
