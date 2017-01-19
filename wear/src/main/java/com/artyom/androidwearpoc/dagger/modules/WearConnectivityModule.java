package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;

import com.artyom.androidwearpoc.connectivity.ConnectivityController;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 19/01/2017.
 */
@Module(includes = {WearApplicationContextModule.class, WearEventBusModule.class})
public class WearConnectivityModule {

    @ForApplication
    @Provides
    ConnectivityController connectivityController(Context appContext, EventBus eventBus){
        return new ConnectivityController(appContext, eventBus);
    }
}
