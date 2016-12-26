package com.artyom.androidwearpoc.shared.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Artyom on 26/12/2016.
 */

public class SensorEventsPackage implements Parcelable{

    private ArrayList<SensorEventData> sensorEvents;

    private float batteryLevel;

    public ArrayList<SensorEventData> getSensorEvents() {
        return sensorEvents;
    }

    public void setSensorEvents(ArrayList<SensorEventData> sensorEvents) {
        this.sensorEvents = sensorEvents;
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.wr(sensorEvents);
    }
}