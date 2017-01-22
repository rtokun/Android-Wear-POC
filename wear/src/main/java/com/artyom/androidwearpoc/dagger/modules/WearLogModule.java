package com.artyom.androidwearpoc.dagger.modules;

import android.app.Application;
import android.content.Context;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.log.MyWearLogger;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 16-Jan-17.
 */
@Module(includes = {WearApplicationContextModule.class, WearSharedPrefsModule.class})
public class WearLogModule {

    @ForApplication
    @Provides
    MyWearLogger myLogger(Context appContext, WearSharedPrefsController sharedPrefsController) {
        return new MyWearLogger(appContext, sharedPrefsController);
    }
}
