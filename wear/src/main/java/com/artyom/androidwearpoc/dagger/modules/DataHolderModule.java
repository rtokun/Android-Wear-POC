package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.data.DataTransferHolder;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 27/12/2016.
 */
@Module
public class DataHolderModule {

    @ForApplication
    @Provides
    DataTransferHolder dataTransferHolder(){
        return new DataTransferHolder();
    }
}
