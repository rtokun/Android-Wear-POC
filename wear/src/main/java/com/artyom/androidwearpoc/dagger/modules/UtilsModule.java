package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.ServiceUtils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 28/12/2016.
 */
@Module(includes = ApplicationContextModule.class)
public class UtilsModule {

    @ForApplication
    @Provides
    ServiceUtils serviceUtils(Context applicationContext){
        return new ServiceUtils(applicationContext);
    }

}
