package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artyom on 12/01/2017.
 */

public class UpdateRateMessage implements Parcelable {

    private int newRate;

    public UpdateRateMessage(int newRate) {
        this.newRate = newRate;
    }

    protected UpdateRateMessage(Parcel in) {
        newRate = in.readInt();
    }

    public static final Creator<UpdateRateMessage> CREATOR = new Creator<UpdateRateMessage>() {

        @Override
        public UpdateRateMessage createFromParcel(Parcel in) {
            return new UpdateRateMessage(in);
        }

        @Override
        public UpdateRateMessage[] newArray(int size) {
            return new UpdateRateMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(newRate);
    }

    public int getNewRate() {
        return newRate;
    }

    public void setNewRate(int newRate) {
        this.newRate = newRate;
    }
}
