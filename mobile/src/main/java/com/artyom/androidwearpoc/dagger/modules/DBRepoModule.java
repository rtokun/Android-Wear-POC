package com.artyom.androidwearpoc.dagger.modules;

import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepoImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tomerlev on 28/12/2016.
 */

@Module
public class DBRepoModule {

    @Provides
    AccelerometerSamplesRepo accelerometerSamplesRepo(){
        return new AccelerometerSamplesRepoImpl();
    }
}
