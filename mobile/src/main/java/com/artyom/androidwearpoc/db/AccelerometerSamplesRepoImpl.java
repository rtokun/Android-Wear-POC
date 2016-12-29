package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;

import java.util.List;

import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by tomerlev on 27/12/2016.
 */
public class AccelerometerSamplesRepoImpl implements AccelerometerSamplesRepo{

    @Override
    public void saveSamples(final List<AccelerometerSample> samples) {
        Timber.i("Starting to save acc samples to database, size: %d", samples.size());
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealm(samples);
        mRealm.commitTransaction();
    }

    @Override
    public void deleteAll() {
        Timber.i("Deleting all accelerometer data from DB ...");
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(AccelerometerSample.class);
        realm.commitTransaction();
        Timber.i("All accelerometer data has been deleted");
    }
}
