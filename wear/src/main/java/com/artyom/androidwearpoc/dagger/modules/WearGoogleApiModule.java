package com.artyom.androidwearpoc.dagger.modules;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Artyom on 24/12/2016.
 */
@Module
public class WearGoogleApiModule {

    private Context mContext;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;

    public WearGoogleApiModule(Context context,
                               GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                               GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.mContext = context;
        mConnectionCallbacks = connectionCallbacks;
        mOnConnectionFailedListener = onConnectionFailedListener;
    }

    @Provides
    GoogleApiClient googleApiClient() {
        return new GoogleApiClient.Builder(mContext.getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();

    }


}
