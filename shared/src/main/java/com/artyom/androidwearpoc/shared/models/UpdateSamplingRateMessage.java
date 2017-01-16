package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artyom on 12/01/2017.
 */

public class UpdateSamplingRateMessage implements Parcelable {

    private int samplingRate;

    public UpdateSamplingRateMessage(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    protected UpdateSamplingRateMessage(Parcel in) {
        samplingRate = in.readInt();
    }

    public static final Creator<UpdateSamplingRateMessage> CREATOR = new Creator<UpdateSamplingRateMessage>() {

        @Override
        public UpdateSamplingRateMessage createFromParcel(Parcel in) {
            return new UpdateSamplingRateMessage(in);
        }

        @Override
        public UpdateSamplingRateMessage[] newArray(int size) {
            return new UpdateSamplingRateMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(samplingRate);
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }
}
