package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;

/**
 * Created by tomerlev on 27/12/2016.
 */

public interface AccelerometerSamplesRepo {

    void saveSamples(final Iterable<AccelerometerSample> samples);

    void deleteAll();

}
