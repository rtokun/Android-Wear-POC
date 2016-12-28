package com.artyom.androidwearpoc.wear.data;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepoImpl;
import com.artyom.androidwearpoc.model.AccelerometerSample;
import com.artyom.androidwearpoc.model.converter.AccelerometerSamplesConverter;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Artyom on 27/12/2016.
 */

public class DataReceiverService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private GoogleApiClient mGoogleApiClient;

    private AccelerometerSamplesRepo mAccelerometerSamplesRepo = new AccelerometerSamplesRepoImpl();

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this.getApplicationContext(), this, this))
                .build()
                .googleApiClient();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

        // Running through all the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

               /* processData(event);
                dele*/
                byte[] data = event.getDataItem().getData();
                MessagePackage message = ParcelableUtil.unmarshall(data, MessagePackage.CREATOR);
                List<AccelerometerSampleData> accelerometerSamples = message.getmAccelerometerSamples();
                List<AccelerometerSample> converted = AccelerometerSamplesConverter.convert(accelerometerSamples);
                mAccelerometerSamplesRepo.saveSamples(converted);

                float batteryPercentage = message.getmBatteryPercentage();

            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }

    private boolean validateConnection() {

        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mGoogleApiClient
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        return result.isSuccess();
    }
}
