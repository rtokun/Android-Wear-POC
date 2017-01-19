package com.artyom.androidwearpoc.shared.models;

/**
 * Created by Artyom on 28/12/2016.
 */

public class ConnectivityStatus {

    private final boolean isConnected;

    public ConnectivityStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
