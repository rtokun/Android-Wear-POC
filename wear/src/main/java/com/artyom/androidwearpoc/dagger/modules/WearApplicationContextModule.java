package com.artyom.androidwearpoc.dagger.modules;

import android.content.Context;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class WearApplicationContextModule {

    private final Context mApplicationContext;

    public WearApplicationContextModule(Context myApplication) {
        this.mApplicationContext = myApplication;
    }

    @ForApplication
    @Provides
    Context application() {
        return mApplicationContext;
    }

}
