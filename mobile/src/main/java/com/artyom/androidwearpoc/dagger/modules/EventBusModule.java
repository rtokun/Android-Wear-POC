package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 28/12/2016.
 */
@Module
public class EventBusModule {

    @Provides
    @ForApplication
    EventBus eventBus(){
        return EventBus.getDefault();
    }

}
