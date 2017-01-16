package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artyom on 12/01/2017.
 */

public class UpdateNumberMessage implements Parcelable {

    private int newNumber;

    public UpdateNumberMessage(int newNumber) {
        this.newNumber = newNumber;
    }

    protected UpdateNumberMessage(Parcel in) {
        newNumber = in.readInt();
    }

    public static final Creator<UpdateNumberMessage> CREATOR = new Creator<UpdateNumberMessage>() {

        @Override
        public UpdateNumberMessage createFromParcel(Parcel in) {
            return new UpdateNumberMessage(in);
        }

        @Override
        public UpdateNumberMessage[] newArray(int size) {
            return new UpdateNumberMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(newNumber);
    }

    public int getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(int newNumber) {
        this.newNumber = newNumber;
    }
}
