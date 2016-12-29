package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.BatteryLevelSample;

/**
 * Created by tomerlev on 29/12/2016.
 */

public interface BatteryLevelSamplesRepo {

    void saveSample(BatteryLevelSample sample);

    void deleteAll();

}
