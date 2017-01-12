package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.modules.DataHolderModule;
import com.artyom.androidwearpoc.dagger.modules.EventBusModule;
import com.artyom.androidwearpoc.dagger.modules.MeasurementModule;
import com.artyom.androidwearpoc.dagger.modules.SharedPrefsModule;
import com.artyom.androidwearpoc.dagger.modules.UtilsModule;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.data.processing.DataProcessingService;
import com.artyom.androidwearpoc.measurement.MeasurementService;
import com.artyom.androidwearpoc.ui.MainActivity;
import com.artyom.androidwearpoc.util.SharedPrefsController;

import dagger.Component;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */
@ForApplication
@Component(modules = {MeasurementModule.class, DataHolderModule.class, UtilsModule.class,
        EventBusModule.class, SharedPrefsModule.class})
public interface WearApplicationComponent {



    void inject(MyWearApplication myWearApplication);

    void inject(MeasurementService measurementService);

    void inject(DataProcessingService dataProcessingService);

    void inject(MainActivity mainActivity);
}
