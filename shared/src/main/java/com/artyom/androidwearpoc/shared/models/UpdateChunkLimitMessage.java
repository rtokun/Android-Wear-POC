package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artyom on 12/01/2017.
 */

public class UpdateChunkLimitMessage implements Parcelable {

    private int samplesPerChunk;

    public UpdateChunkLimitMessage(int samplesPerChunk) {
        this.samplesPerChunk = samplesPerChunk;
    }

    protected UpdateChunkLimitMessage(Parcel in) {
        samplesPerChunk = in.readInt();
    }

    public static final Creator<UpdateChunkLimitMessage> CREATOR = new Creator<UpdateChunkLimitMessage>() {

        @Override
        public UpdateChunkLimitMessage createFromParcel(Parcel in) {
            return new UpdateChunkLimitMessage(in);
        }

        @Override
        public UpdateChunkLimitMessage[] newArray(int size) {
            return new UpdateChunkLimitMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(samplesPerChunk);
    }

    public int getSamplesPerChunk() {
        return samplesPerChunk;
    }

    public void setSamplesPerChunk(int samplesPerChunk) {
        this.samplesPerChunk = samplesPerChunk;
    }
}
