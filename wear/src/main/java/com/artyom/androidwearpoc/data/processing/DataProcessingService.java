package com.artyom.androidwearpoc.data.processing;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Artyom on 25/12/2016.
 */

public class DataProcessingService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataProcessingService() {
        super("DataProcessingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
