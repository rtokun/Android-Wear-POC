package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;

public class AccelerometerSampleData extends BaseSensorSampleData {

    private float x;

    private float y;

    private float z;

    public AccelerometerSampleData(long timestamp, float x, float y, float z) {
        super(timestamp);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AccelerometerSampleData(Parcel in) {
        super(in);
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
    }


    public static final Creator<AccelerometerSampleData> CREATOR = new Creator<AccelerometerSampleData>() {

        @Override
        public AccelerometerSampleData createFromParcel(Parcel in) {
            return new AccelerometerSampleData(in);
        }

        @Override
        public AccelerometerSampleData[] newArray(int size) {
            return new AccelerometerSampleData[size];
        }
    };

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
