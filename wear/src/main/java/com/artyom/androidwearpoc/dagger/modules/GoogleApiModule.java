package com.artyom.androidwearpoc.dagger.modules;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.Wearable;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 24/12/2016.
 */
@Module
public class GoogleApiModule {

    Context mContext;


    public GoogleApiModule(Context context) {
        this.mContext = context;

    }

    @Provides
    GoogleApiClient googleApiClient() {
        return new GoogleApiClient.Builder(mContext.getApplicationContext())
                .addApi(Wearable.API)
                .build();

    }


}
