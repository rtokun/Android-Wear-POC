package com.artyom.androidwearpoc.model;

import io.realm.RealmObject;


/**
 * Created by tomerlev on 27/12/2016.
 */

import io.realm.RealmObject;

public class AccelerometerSample extends RealmObject {
    private int x;
    private int y;
    private int z;
    private long ts;

    public AccelerometerSample() {
    }

    public AccelerometerSample(int x, int y, int z, long ts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ts = ts;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getTs() {
        return ts;
    }
}

