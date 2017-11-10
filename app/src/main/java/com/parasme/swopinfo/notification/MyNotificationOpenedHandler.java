package com.parasme.swopinfo.notification;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.fragment.FragmentFile;

import org.json.JSONObject;

import static com.parasme.swopinfo.application.MyApplication.appContext;

/**
 * Created by SoNu on 3/9/2017.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        Log.e("OneSignalExample", "customkey set with value: ");

        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (result.notification.displayType.name().equals("InAppAlert")) {
            if(data!=null && MainActivity.activityContext!=null){
                if(data.has("fileid")){
                    Log.e("OneSignalExample11", data.toString());
                    String fileId = data.optString("fileid");
                    if (MainActivity.activityContext.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentFile){
                        Intent intent = new Intent(MainActivity.activityContext, MainActivity.class);
                        intent.putExtra("fromOneSignal", data.toString());
                        MainActivity.activityContext.startActivity(intent);
                        MainActivity.activityContext.finish();
                    }
                    else{
                        Fragment fragment = new FragmentFile();
                        Bundle bundle = new Bundle();
                        bundle.putInt("fildeId",Integer.parseInt(fileId));
                        fragment.setArguments(bundle);
                        if(!MainActivity.activityContext.isFinishing())
                            MainActivity.activityContext.getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commitAllowingStateLoss();
                    }
                }
            }
        }
        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(data!=null)
            intent.putExtra("fromOneSignal",data.toString());
        appContext.startActivity(intent);

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
      */
    }


}
