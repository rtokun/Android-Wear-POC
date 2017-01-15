package com.artyom.androidwearpoc.model;

import java.util.Date;

import io.realm.RealmObject;


/**
 * TODO: DELETE this class
 */
public class AccelerometerSampleTEMPORAL extends RealmObject {
    private int messageIndex;
    private float x;
    private float y;
    private float z;
    private long ts;
    private String ds;

    public AccelerometerSampleTEMPORAL() {
    }

    public AccelerometerSampleTEMPORAL(int messageIndex, float x, float y, float z, long ts,
                                       String dateString) {
        this.messageIndex = messageIndex;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ts = ts;
        ds = dateString;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
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

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }
}

