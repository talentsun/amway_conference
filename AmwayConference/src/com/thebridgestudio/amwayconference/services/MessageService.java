package com.thebridgestudio.amwayconference.services;

import com.thebridgestudio.amwayconference.Intents;

import android.app.IntentService;
import android.content.Intent;

public class MessageService extends IntentService {

    public MessageService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intents.ACTION_SYNC_MESSAGE.equalsIgnoreCase(action)) {
                //sync server
                
            }
        }
        
    }

}
