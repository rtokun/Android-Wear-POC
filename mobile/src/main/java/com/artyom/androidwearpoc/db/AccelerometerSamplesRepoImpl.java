package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;

import java.util.List;

import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by tomerlev on 27/12/2016.
 */
public class AccelerometerSamplesRepoImpl implements AccelerometerSamplesRepo{

    //private Realm mRealm = Realm.getDefaultInstance();

    @Override
    public void saveSamples(final List<AccelerometerSample> samples) {
        //TODO: Async
        Timber.i("Starting to save acc samples to database, size: %d", samples.size());
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealm(samples);
        mRealm.commitTransaction();
        /*Realm.Transaction transaction = new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                Timber.i("Starting to save acc samples to database, size: %d", samples.size());
                realm.copyFromRealm(samples);
            }
        };
        mRealm.executeTransactionAsync(transaction, new Realm.Transaction.OnSuccess(){

            @Override
            public void onSuccess() {
                Timber.i("Acc samples saved to db");
            }
        }, new Realm.Transaction.OnError(){

            @Override
            public void onError(Throwable error) {
                Timber.i("Acc samples saving to db failed %s", error.getMessage());
            }
        });*/
    }
}
