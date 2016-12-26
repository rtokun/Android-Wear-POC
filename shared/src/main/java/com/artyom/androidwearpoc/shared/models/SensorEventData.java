package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorEventData implements Parcelable {

    private long timestamp;

    private int sensorType;

    private int accuracy;

    private float[] values;

    public SensorEventData(long timestamp, int sensorType, int accuracy, float[] values) {
        this.timestamp = timestamp;
        this.sensorType = sensorType;
        this.accuracy = accuracy;
        this.values = values;
    }

    public SensorEventData(Parcel in) {
        timestamp = in.readLong();
        sensorType = in.readInt();
        accuracy = in.readInt();
        values = in.createFloatArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeInt(sensorType);
        dest.writeInt(accuracy);
        dest.writeFloatArray(values);
    }

    public static final Creator<SensorEventData> CREATOR = new Creator<SensorEventData>() {

        @Override
        public SensorEventData createFromParcel(Parcel in) {
            return new SensorEventData(in);
        }

        @Override
        public SensorEventData[] newArray(int size) {
            return new SensorEventData[size];
        }
    };

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }
}
