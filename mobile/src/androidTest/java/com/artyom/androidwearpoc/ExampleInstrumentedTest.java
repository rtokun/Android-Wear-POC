package com.artyom.androidwearpoc;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;


import com.artyom.androidwearpoc.model.AccelerometerSample;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

   @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.artyom.androidwearpoc", appContext.getPackageName());

       Realm.init(appContext);
       Realm realm = Realm.getDefaultInstance();
       generateRandomAccelerometerSamples(realm);
       readSamplesFromDB(realm);
    }

    private void generateRandomAccelerometerSamples(Realm realm){
        long ts = System.currentTimeMillis();
        realm.beginTransaction();
        for(int i=0; i< 300; i++){
            AccelerometerSample sample = realm.createObject(AccelerometerSample.class);
            sample.setTs(ts + i*20);
            sample.setX(i);
            sample.setY(i);
            sample.setZ(i);

            if (i % 50 == 0) {
                realm.commitTransaction();
                realm.beginTransaction();
            }
        }
    }

    private void readSamplesFromDB(Realm realm) {
        RealmResults<AccelerometerSample> all = realm.where(AccelerometerSample.class).findAll();
        int size = all.size();
        all.deleteAllFromRealm();
    }
}
