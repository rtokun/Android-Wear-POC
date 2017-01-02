package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSampleTEMPORAL;

/**
 * Created by tomerlev on 27/12/2016.
 */

public interface AccelerometerSamplesRepo {

    //TODO: revert back to - void saveSamples(final Iterable<AccelerometerSample> samples);

    void saveSamples(final Iterable<AccelerometerSampleTEMPORAL> samples);

    void deleteAll();

    long count();

}
