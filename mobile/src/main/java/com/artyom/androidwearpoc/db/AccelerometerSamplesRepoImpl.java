package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;
import com.artyom.androidwearpoc.model.AccelerometerSampleTEMPORAL;

import io.realm.Realm;
import timber.log.Timber;


/**
 * Created by tomerlev on 27/12/2016.
 */
public class AccelerometerSamplesRepoImpl extends BaseRepo<AccelerometerSampleTEMPORAL> implements
        AccelerometerSamplesRepo{

    public AccelerometerSamplesRepoImpl() {
        super(AccelerometerSampleTEMPORAL.class);
    }

    //TODO: remove this method and use the one from BaseRepo - without the int messageIndex
    public void saveSamples(final Iterable<AccelerometerSampleTEMPORAL> samples) {
        Timber.i("Starting to save samples of type %s to sb", AccelerometerSampleTEMPORAL.class.getName());
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealm(samples);
        mRealm.commitTransaction();
        Timber.i("All of type %s has been saved to db", AccelerometerSampleTEMPORAL.class.getName());
    }
}
