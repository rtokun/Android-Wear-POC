package com.artyom.androidwearpoc.data;

import com.artyom.androidwearpoc.shared.models.SamplesChunk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Artyom on 27/12/2016.
 */
public class DataTransferHolder {

    private Map<Long, SamplesChunk> queueOfMessagePackagesToSend;

    public DataTransferHolder() {
        queueOfMessagePackagesToSend = new HashMap<>();
    }

    public Map<Long, SamplesChunk> getQueueOfMessagePackages() {
        return queueOfMessagePackagesToSend;
    }

    public void setQueueOfMessagePackages(Map<Long, SamplesChunk> queueOfMessagePackages) {
        this.queueOfMessagePackagesToSend = queueOfMessagePackages;
    }
}
