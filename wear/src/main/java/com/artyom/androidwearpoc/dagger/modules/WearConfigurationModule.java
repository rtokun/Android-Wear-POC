package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.WearConfigController;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 11/01/2017.
 */
@Module(includes = {WearSharedPrefsModule.class, WearEventBusModule.class})
public class WearConfigurationModule {

    @ForApplication
    @Provides
    WearConfigController configController(WearSharedPrefsController sharedPrefsController,
                                          EventBus eventBus){
        return new WearConfigController(sharedPrefsController, eventBus);
    }

}
