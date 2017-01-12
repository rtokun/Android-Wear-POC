package com.artyom.androidwearpoc.wear.communication;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Artyom-IDEO on 12-Jan-17.
 */

public class CommunicationService extends IntentService {

    public CommunicationService() {
        super("CommunicationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
    }
}
