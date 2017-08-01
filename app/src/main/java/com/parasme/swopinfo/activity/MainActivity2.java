package com.parasme.swopinfo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.parasme.swopinfo.R;

/**
 * Main Activity for sending App Invites and launching the DeepLinkActivity when an
 * App Invite is received.
 */
public class MainActivity2 extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = MainActivity2.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    // [START define_variables]
    private GoogleApiClient mGoogleApiClient;
    // [END define_variables]

    // [START on_create]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Invite button click listener
        findViewById(R.id.invite_button).setOnClickListener(this);
        // [END_EXCLUDE]

        // Create an auto-managed GoogleApiClient with access to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.e(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });
    }
    // [END on_create]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage("Google Error1");
    }

    /**
     * User has clicked the 'Invite' button, launch the invitation UI with the proper
     * title, message, and deep link
     */
    // [START on_invite_clicked]
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Invite Title")
                .setMessage("Invite Message")
                .setDeepLink(Uri.parse("http://example.com/offer/five_dollar_offer"))
                .setCustomImage(Uri.parse("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"))
                .setCallToActionText("Install 11")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    // [END on_invite_clicked]

    // [START on_activity_result]

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.e(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                showMessage("Send Failed 11");
                // [END_EXCLUDE]
            }
        }
    }
    // [END on_activity_result]

    private void showMessage(String msg) {
        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.invite_button) {
            onInviteClicked();
        }
    }
}