package com.artyom.androidwearpoc.db;


import io.realm.Realm;
import io.realm.RealmObject;
import timber.log.Timber;

/**
 * Base realm db repository class.
 * This class can be used to provide all basic db operations on realm objects.
 */
public abstract class BaseRepo<T extends RealmObject> {

    private Class clazz;

    public BaseRepo(Class clazz) {
        this.clazz = clazz;
    }

    public void saveSample(final T sample) {
        Timber.i("Starting to save sample of type %s to db", clazz.getName());
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealm(sample);
        mRealm.commitTransaction();
        Timber.i("Sample of type %s has been saved to db", clazz);
    }

    public void saveSamples(final Iterable<T> samples) {
        Timber.i("Starting to save samples of type %s to sb", clazz.getName());
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        mRealm.copyToRealm(samples);
        mRealm.commitTransaction();
        Timber.i("All of type %s has been saved to db", clazz);
    }

    public void deleteAll() {
        Timber.i("Deleting all samples of type %s data from db ...", clazz);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
        Timber.i("All samples of type %s has been deleted", clazz);
    }

    public long count(){
        Timber.i("Counting samples of type %s ...", clazz);
        Realm realm = Realm.getDefaultInstance();
        return realm.where(clazz).count();
    }
}
