package com.parasme.swopinfo.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Mukesh on 12/12/2016.
 */

public class FbLoginActivty extends Activity {
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private static FbSignInDetail fbSignInDetail;
    private boolean isRunningFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //Hash key generater code
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.parasme.yeepi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email","user_location","user_posts","user_birthday","user_relationships"));

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        if (isLoggedIn()){
            //LoginManager.getInstance().logOut();
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email"));
        }
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e("current profile chnaged","yes");
               // fbSignInDetail.onGetFBAccountDetail(newProfile);

            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newGraphPathRequest(
                                loginResult.getAccessToken(),
                                "/me?fields=id,name,cover,birthday,posts,email,devices,location,relationship_status",
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        // Insert your code here
                                        Log.e("BHAI",response.getJSONObject().toString());
                                        Log.e("DATA",new Gson().toJson(response));
                                    }
                                });

                        request.executeAsync();

/*
                        // login successful
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());
                                // Get facebook data from login

                                    //fbSignInDetail.onGetFBAccountDetail(object);
                                    //LoginManager.getInstance().logOut();
                                    //finish();

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, gender, cover"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();
*/


                    }

                    @Override
                    public void onCancel() {
                        // login cancelled
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // login error
                    }
                });


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public interface FbSignInDetail{
        void onGetFBAccountDetail(JSONObject newProfile);
    }
    public static void setOnFbClickListener(FbSignInDetail fbSignDetail){
        fbSignInDetail = fbSignDetail;
    }
    private boolean isLoggedIn() {
        AccessToken accesstoken = AccessToken.getCurrentAccessToken();
        return !(accesstoken == null || accesstoken.getPermissions().isEmpty());
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!isRunningFirstTime)
            finish();
        isRunningFirstTime=false;
    }
}
