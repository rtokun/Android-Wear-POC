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

    ConnectionCallbacks mConnectionCallbacks;

    OnConnectionFailedListener mOnConnectionFailedListener;

    public GoogleApiModule(Context context,
                           ConnectionCallbacks connectionCallbacks,
                           OnConnectionFailedListener onConnectionFailedListener) {
        this.mContext = context;
        this.mConnectionCallbacks = connectionCallbacks;
        this.mOnConnectionFailedListener = onConnectionFailedListener;
    }

    @Provides
    GoogleApiClient googleApiClient() {
        return new GoogleApiClient.Builder(mContext.getApplicationContext())
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Wearable.API)
                .build();

    }


}
