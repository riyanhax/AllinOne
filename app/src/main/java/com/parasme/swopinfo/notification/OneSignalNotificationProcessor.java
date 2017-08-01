package com.parasme.swopinfo.notification;

/**
 * Created by SoNu on 3/9/2017.
 */

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;

import java.math.BigInteger;

public class OneSignalNotificationProcessor extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        Log.e("ONESIGNAL", "Notification displayed with id: " + notification.toString());

        // Read any properties you need from notification.
        // Return true if you want to keep a notification from displaying else return false if you dont want to display notification.
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Green on Android 5.0+ devices.
                return builder.setColor(new BigInteger("147956", 16).intValue());
            }
        };
            //OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);

        // Return true to stop the notification from displaying.
        return SharedPreferenceUtility.getInstance().get(AppConstants.PREF_DISABLE_APP_NOTIFICATION,false);

    }
}