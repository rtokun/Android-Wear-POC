package com.artyom.androidwearpoc.wear.data;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Artyom on 27/12/2016.
 */

public class DataReceiverService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

        // Running through all the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
            } else if (event.getType() == DataEvent.TYPE_DELETED){

            }
        }
    }
}
