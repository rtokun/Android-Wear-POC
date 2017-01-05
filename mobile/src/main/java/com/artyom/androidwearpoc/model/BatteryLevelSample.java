package com.artyom.androidwearpoc.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by tomerlev on 29/12/2016.
 */

public class BatteryLevelSample extends RealmObject {
    private float level;
    private Date date;

    public BatteryLevelSample() {
    }

    public BatteryLevelSample(float level, Date date) {
        this.level = level;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
