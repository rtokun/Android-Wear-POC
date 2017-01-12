package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.ConfigController;
import com.artyom.androidwearpoc.util.SharedPrefsController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 11/01/2017.
 */
@Module(includes = SharedPrefsModule.class)
public class ConfigurationModule {

    @ForApplication
    @Provides
    ConfigController configController(SharedPrefsController sharedPrefsController){
        return new ConfigController(sharedPrefsController);
    }

}
