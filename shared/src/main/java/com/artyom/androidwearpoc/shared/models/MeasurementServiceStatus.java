package com.artyom.androidwearpoc.shared.models;

/**
 * Created by Artyom on 28/12/2016.
 */

public class MeasurementServiceStatus {

    private final boolean isRunning;

    public MeasurementServiceStatus(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
