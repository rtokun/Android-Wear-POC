package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.ConfigController;
import com.artyom.androidwearpoc.util.SharedPrefsController;
import com.artyom.androidwearpoc.wear.communication.CommunicationController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 11/01/2017.
 */
@Module(includes = {SharedPrefsModule.class, CommunicationModule.class})
public class ConfigurationModule {

    @ForApplication
    @Provides
    ConfigController configController(SharedPrefsController sharedPrefsController,
                                      CommunicationController communicationController){
        return new ConfigController(sharedPrefsController, communicationController);
    }

}
