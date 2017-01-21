package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.MyLogger;
import com.artyom.androidwearpoc.util.SharedPrefsController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 16-Jan-17.
 */
@Module(includes = SharedPrefsModule.class)
public class LogModule {

    @ForApplication
    @Provides
    MyLogger myLogger(SharedPrefsController sharedPrefsController){
        return new MyLogger(sharedPrefsController);
    }
}
