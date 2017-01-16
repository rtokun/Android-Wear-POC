package com.artyom.androidwearpoc.model.converter;

import com.artyom.androidwearpoc.model.AccelerometerSample;
import com.artyom.androidwearpoc.model.AccelerometerSampleTEMPORAL;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.bytesizebit.androidutils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by tomerlev on 27/12/2016.
 */

public class AccelerometerSamplesConverter {

    public static List<AccelerometerSample> convert(List<AccelerometerSampleData> samples) {
        List<AccelerometerSample> convertedSamples = new LinkedList<>();
        for (AccelerometerSampleData sample : samples) {
            convertedSamples.add(new AccelerometerSample(
                    sample.getX(),
                    sample.getY(),
                    sample.getZ(),
                    sample.getTimestamp()));
        }
        return convertedSamples;
    }

    //TODO: remove this method
    public static List<AccelerometerSampleTEMPORAL> convert(List<AccelerometerSampleData> samples, int
            messageIndex) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
        List<AccelerometerSampleTEMPORAL> convertedSamples = new LinkedList<>();
        for (AccelerometerSampleData sample : samples) {
            convertedSamples.add(new AccelerometerSampleTEMPORAL(messageIndex,
                    sample.getX(),
                    sample.getY(),
                    sample.getZ(),
                    sample.getTimestamp(),
                    DateUtils.millisecondsToString(sample.getTimestamp(), sdf)));
        }
        return convertedSamples;
    }
}
