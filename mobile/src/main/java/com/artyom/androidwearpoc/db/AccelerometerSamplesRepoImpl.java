package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.AccelerometerSample;


/**
 * Created by tomerlev on 27/12/2016.
 */
public class AccelerometerSamplesRepoImpl extends BaseRepo<AccelerometerSample> implements AccelerometerSamplesRepo{

    public AccelerometerSamplesRepoImpl() {
        super(AccelerometerSample.class);
    }
}
