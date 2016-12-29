package com.artyom.androidwearpoc.db;

import com.artyom.androidwearpoc.model.BatteryLevelSample;

/**
 * Created by tomerlev on 29/12/2016.
 */

public class BatteryLevelSamplesRepoImpl extends BaseRepo<BatteryLevelSample> implements BatteryLevelSamplesRepo {

    public BatteryLevelSamplesRepoImpl() {
        super(BatteryLevelSample.class);
    }
}
