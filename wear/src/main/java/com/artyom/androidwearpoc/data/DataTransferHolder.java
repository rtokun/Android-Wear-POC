package com.artyom.androidwearpoc.data;

import com.artyom.androidwearpoc.shared.models.MessagePackage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Artyom on 27/12/2016.
 */
public class DataTransferHolder {

    private Map<Long, MessagePackage> queueOfMessagePackagesToSend;

    public DataTransferHolder() {
        queueOfMessagePackagesToSend = new HashMap<>();
    }

    public Map<Long, MessagePackage> getQueueOfMessagePackages() {
        return queueOfMessagePackagesToSend;
    }

    public void setQueueOfMessagePackages(Map<Long, MessagePackage> queueOfMessagePackages) {
        this.queueOfMessagePackagesToSend = queueOfMessagePackages;
    }
}
