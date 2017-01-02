package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MessagePackage implements Parcelable {

    private float mBatteryPercentage;

    private int mIndex;

    private List<AccelerometerSampleData> mAccelerometerSamples;

    public MessagePackage() {
    }

    protected MessagePackage(Parcel in) {
        mBatteryPercentage = in.readFloat();
        mAccelerometerSamples = in.createTypedArrayList(AccelerometerSampleData.CREATOR);
    }

    public static final Creator<MessagePackage> CREATOR = new Creator<MessagePackage>() {

        @Override
        public MessagePackage createFromParcel(Parcel in) {
            return new MessagePackage(in);
        }

        @Override
        public MessagePackage[] newArray(int size) {
            return new MessagePackage[size];
        }
    };

    //region Setters and Getters
    public List<AccelerometerSampleData> getAccelerometerSamples() {
        return mAccelerometerSamples;
    }

    public void setAccelerometerSamples(List<AccelerometerSampleData> accelerometerSamples) {
        this.mAccelerometerSamples = accelerometerSamples;
    }

    public float getBatteryPercentage() {
        return mBatteryPercentage;
    }

    public void setBatteryPercentage(float mBatteryPercentage) {
        this.mBatteryPercentage = mBatteryPercentage;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }
    //endregion

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mBatteryPercentage);
        dest.writeTypedList(mAccelerometerSamples);
    }
}
