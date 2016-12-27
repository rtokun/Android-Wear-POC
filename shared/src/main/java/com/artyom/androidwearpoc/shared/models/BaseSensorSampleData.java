package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artyom on 27/12/2016.
 */

public abstract class BaseSensorSampleData implements Parcelable {

    protected long timestamp;

    protected BaseSensorSampleData(Parcel in) {
        timestamp = in.readLong();
    }

    public BaseSensorSampleData(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
