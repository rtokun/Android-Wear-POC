package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.wear.communication.CommunicationController;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom-IDEO on 12-Jan-17.
 */
@Module(includes = ApplicationContextModule.class)
public class CommunicationModule {

    @ForApplication
    @Provides
    CommunicationController communicationController(Context applicationContext){
        return new CommunicationController(applicationContext);
    }

}
