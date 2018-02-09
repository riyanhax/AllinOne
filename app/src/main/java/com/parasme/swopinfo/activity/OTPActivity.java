package com.parasme.swopinfo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import net.alhazmy13.catcho.library.Catcho;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class OTPActivity extends AppCompatActivity {



    EditText editOTP;
    TextView textCreateAccount;
    Button btnSubmit;
    String userTempId, playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());

        userTempId = getIntent().getStringExtra("id");
        editOTP = (EditText) findViewById(R.id.editOTP);
        textCreateAccount = (TextView) findViewById(R.id.textCreateAccount);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog registerDialog = Utils.loadRegisterDialog(OTPActivity.this);
                Button btnIndividual = (Button) registerDialog.findViewById(R.id.btnIndividual);
                Button btnBusiness = (Button) registerDialog.findViewById(R.id.btnBusiness);

                btnIndividual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDialog.dismiss();
                        Intent i = new Intent(OTPActivity.this, SignUpActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                    }
                });

                btnBusiness.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDialog.dismiss();
                        Intent i = new Intent(OTPActivity.this, SubscriptionActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                    }
                });
                registerDialog.show();

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmit();
            }
        });
    }


    private void validateAndSubmit() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                String text = "OneSignal UserID:\n" + userId + "\n\n";
                playerId = userId;
                Log.e("ONESIN", "idsAvailable: " + userId + "____" + registrationId);
                if (!userId.equals(""))
                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_PLAYER_ID, userId);
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_GCM_TOKEN, registrationId);
            }
        });


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        String otp=editOTP.getText().toString();

        if(otp.equals("")){
            editOTP.setError("Field cannot be empty");
            editOTP.requestFocus();
            return;
        }


        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"RegTempID","OTP","player_id"},new String[]{userTempId, otp, playerId});
        WebServiceHandler webServiceHandler=new WebServiceHandler(OTPActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("MATCHOTP",response);
                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    final String status = jsonObject.optString("status");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status.equals("1")) {
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_LOGIN, true);
                                JSONObject returnObject = jsonObject.optJSONObject("returnvalue");
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_ID, returnObject.optInt("userid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_NAME, returnObject.optString("username"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_EMAIL, returnObject.optString("userEmail"));

                                // Decoding encoded emojis if exists
                                String firstName = EmojiHandler.decodeJava(returnObject.optString("userFirstname"));
                                String lastName = EmojiHandler.decodeJava(returnObject.optString("userLastname"));

                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_FIRST_NAME, firstName);
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_SUR_NAME, lastName);
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_COMPANY_ID, returnObject.optInt("companyid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_NOTIFICATION, returnObject.optBoolean("ReceiveEmailNotifications"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_COUNTRY, returnObject.optString("country"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BDAY, returnObject.optString("bday"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_TITLE, returnObject.optString("businessTitle"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_EMAIL, returnObject.optString("businessEmail"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CELL, returnObject.optString("businessCell"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_TEL, returnObject.optString("businessDirecttel"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_FAX, returnObject.optString("businesscustomfax"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CUSTOMF1, returnObject.optString("businesscustomfield1"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CUSTOMF2, returnObject.optString("businesscustomfield2"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_STATUS, returnObject.optString("businessstatus"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_EMP_DATE, returnObject.optString("businessempdate"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_PROFESSION, returnObject.optString("businessprofession"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_COMPANY, returnObject.optString("businessCompanyName"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_AUTH_TOKEN, returnObject.optString("authenticationtoken"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_OWN_COMPANY_ID, returnObject.optInt("companyid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_IS_BUSINESS, returnObject.optInt("companyid") == 0 ? false : true);

                                AppConstants.AUTH_TOKEN = returnObject.optString("authenticationtoken");
                                String userId = returnObject.optInt("userid") + "";
                                AppConstants.USER_ID = userId;
                                if (SignUpActivity.imagePath.equals("")){
                                    Intent intent;
                                    if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO, false))
                                        intent = new Intent(OTPActivity.this, MainActivity.class);
                                    else
                                        intent = new Intent(OTPActivity.this, FlowActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                    updatePic(null, SignUpActivity.imagePath, returnObject.optInt("userid"));
                            } else
                                MyApplication.alertDialog(OTPActivity.this, jsonObject.optString("message"), "Alert");
                        }
                    });

                }catch (JSONException e){e.printStackTrace();}
            }
        };

        try {
            webServiceHandler.post(AppConstants.URL_MATCH_OTP,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void updatePic(final Bitmap bitmap, String imagePath, int userId) {
        WebServiceHandler webServiceHandler=new WebServiceHandler(OTPActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Pictt Resp",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                Intent intent;
                                if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO, false))
                                    intent = new Intent(OTPActivity.this, MainActivity.class);
                                else
                                    intent = new Intent(OTPActivity.this, FlowActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                MyApplication.alertDialog(OTPActivity.this,"Could not register","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_USER +"profilepic/" + userId , WebServiceHandler.createMultiPartBuilder(new String[]{"profilepic"},new String[]{imagePath}),userId+"");
        }catch (IOException e){}
    }

}