package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MyWearApplication;
import com.artyom.androidwearpoc.dagger.modules.WearConfigurationModule;
import com.artyom.androidwearpoc.dagger.modules.WearDataHolderModule;
import com.artyom.androidwearpoc.dagger.modules.WearEventBusModule;
import com.artyom.androidwearpoc.dagger.modules.WearMeasurementModule;
import com.artyom.androidwearpoc.dagger.modules.WearSharedPrefsModule;
import com.artyom.androidwearpoc.dagger.modules.WearUtilsModule;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.data.processing.DataProcessingService;
import com.artyom.androidwearpoc.measurement.MeasurementService;
import com.artyom.androidwearpoc.ui.MainActivity;
import com.artyom.androidwearpoc.util.WearConfigController;

import org.greenrobot.eventbus.EventBus;

import dagger.Component;

/**
 * Created by Artyom-IDEO on 25-Dec-16.
 */
@ForApplication
@Component(modules = {WearMeasurementModule.class,
        WearDataHolderModule.class,
        WearUtilsModule.class,
        WearEventBusModule.class,
        WearSharedPrefsModule.class,
        WearConfigurationModule.class})
public interface WearApplicationComponent {

    EventBus getEventBus();

    WearConfigController getWearConfigController();

    void inject(MyWearApplication myWearApplication);

    void inject(MeasurementService measurementService);

    void inject(DataProcessingService dataProcessingService);

    void inject(MainActivity mainActivity);
}
