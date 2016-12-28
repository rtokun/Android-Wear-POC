package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;

import java.util.List;

/**
 * Created by tomerlev on 27/12/2016.
 */

public interface AccelerometerSamplesRepo {

    void saveSamples(List<AccelerometerSample> samples);

}
