package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.WearSharedPrefsController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */
@Module(includes = ApplicationContextModule.class)
public class SharedPrefsModule {

    @ForApplication
    @Provides
    WearSharedPrefsController sharedPrefsController(Context applicationContext){
        return new WearSharedPrefsController(applicationContext);
    }

}
