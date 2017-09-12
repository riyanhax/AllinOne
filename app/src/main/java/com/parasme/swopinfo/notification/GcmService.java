package com.parasme.swopinfo.notification;

import android.os.Bundle;
import android.util.Log;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by SoNu on 5/17/2017.
 */

public class GcmService extends GcmListenerService {



    @Override
    public void onMessageReceived(String from, Bundle data) {

        sendNotification("Received GCM Message: " + data.toString());
        if(MobiComPushReceiver.isMobiComPushNotification(data)) {
            MobiComPushReceiver.processMessageAsync(this, data);
            return;
        }
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Log.e("GCM",msg);
    }
}
