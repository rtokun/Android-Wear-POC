package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;

public class AccelerometerSampleData extends BaseSensorSampleData {

    private float[] values;

    public AccelerometerSampleData(long timestamp, float[] values) {
        super(timestamp);
        this.values = values;
    }

    public AccelerometerSampleData(Parcel in) {
        super(in);
        values = in.createFloatArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeFloatArray(values);
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
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


}
