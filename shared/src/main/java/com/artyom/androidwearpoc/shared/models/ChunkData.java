package com.artyom.androidwearpoc.shared.models;

/**
 * Created by Artyom-IDEO on 10-Jan-17.
 */

public class ChunkData {

    private int packageSize;

    private long firstSampleTimestamp;

    private long lastSampleTimestamp;

    private int packageIndex;


    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public long getFirstSampleTimestamp() {
        return firstSampleTimestamp;
    }

    public void setFirstSampleTimestamp(long firstSampleTimestamp) {
        this.firstSampleTimestamp = firstSampleTimestamp;
    }

    public long getLastSampleTimestamp() {
        return lastSampleTimestamp;
    }

    public void setLastSampleTimestamp(long lastSampleTimestamp) {
        this.lastSampleTimestamp = lastSampleTimestamp;
    }

    public int getPackageIndex() {
        return packageIndex;
    }

    public void setPackageIndex(int packageIndex) {
        this.packageIndex = packageIndex;
    }
}
