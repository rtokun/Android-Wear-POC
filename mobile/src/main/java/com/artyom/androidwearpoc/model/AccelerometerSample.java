package com.artyom.androidwearpoc.model;

import io.realm.RealmObject;


/**
 * Created by tomerlev on 27/12/2016.
 */


public class AccelerometerSample extends RealmObject {
    private float x;
    private float y;
    private float z;
    private long ts;

    public AccelerometerSample() {
    }

    public AccelerometerSample(float x, float y, float z, long ts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ts = ts;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public long getTs() {
        return ts;
    }
}

