package com.artyom.androidwearpoc.model.converter;

import com.artyom.androidwearpoc.model.AccelerometerSample;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tomerlev on 27/12/2016.
 */

public class AccelerometerSamplesConverter {

    private static final long ROUND_RANGE = 1000000;

    public static List<AccelerometerSample> convert(List<AccelerometerSampleData> samples){
        List<AccelerometerSample> convertedSamples = new LinkedList<>();
        for (AccelerometerSampleData sample : samples) {
            float[] values = sample.getValues();
            convertedSamples.add(new AccelerometerSample(
                    values[0],
                    values[1],
                    values[2],
                    sample.getTimestamp() / ROUND_RANGE));
        }
        return convertedSamples;
    }
}
