package com.artyom.androidwearpoc.model;

import io.realm.RealmObject;

/**
 * Created by tomerlev on 29/12/2016.
 */

public class BatteryLevelSample extends RealmObject {
    private float level;

    public BatteryLevelSample() {
    }

    public BatteryLevelSample(float level) {
        this.level = level;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}
