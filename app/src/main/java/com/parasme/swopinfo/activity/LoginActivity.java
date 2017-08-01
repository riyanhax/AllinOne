package com.parasme.swopinfo.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentSubscription;
import com.parasme.swopinfo.fragment.FragmentSubscriptionPayment;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import net.alhazmy13.catcho.library.Catcho;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.FormBody;

import static com.parasme.swopinfo.application.MyApplication.initOneSignal;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private boolean isWriteAllowed=false;
    private boolean isCameraAllowed=false;
    private WebView webView;
    private ProgressBar progressBar;
    private boolean isURLShown = false;
    private String playerId="";

    @ViewById EditText editUserName;
    @ViewById EditText editPassword;

    @Click(R.id.textForgot)
    void forgot(){
        startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity_.class));
    }

    @Click(R.id.textCreateAccount)
    void click(){
        final Dialog registerDialog = Utils.loadRegisterDialog(LoginActivity.this);
        Button btnIndividual = (Button) registerDialog.findViewById(R.id.btnIndividual);
        Button btnBusiness = (Button) registerDialog.findViewById(R.id.btnBusiness);

        btnIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog.dismiss();
                Intent i = new Intent(LoginActivity.this, SignUpActivity_.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
            }
        });

        btnBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDialog.dismiss();
                Dialog dialog = loadDialog();
            }
        });
        registerDialog.show();
    }

    @Click(R.id.btnLogin)
    void clickLoginButton(){
        if(isWriteAllowed && isCameraAllowed)
            validateAndLogin();
        else
            askPermissions();
    }

    @AfterViews
    protected void init(){
        askPermissions();
    }

    private void validateAndLogin() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                String text = "OneSignal UserID:\n" + userId + "\n\n";
                playerId = userId;
                Log.e("ONESIN", "idsAvailable: "+userId +"____"+registrationId);
                if(!userId.equals(""))
                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_PLAYER_ID,userId);
                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_GCM_TOKEN,registrationId);
                if (registrationId != null)
                    text += "Google Registration Id:\n" + registrationId;
                else
                    text += "Google Registration Id:\nCould not subscribe for push";
            }
        });

        String userName=editUserName.getText().toString();
        String userPassword=editPassword.getText().toString();

        if(userName.equals("")){
            editUserName.setError("Field cannot be empty");
            editUserName.requestFocus();
            return;
        }

        if(userPassword.equals("")){
            editPassword.setError("Field cannot be empty");
            editPassword.requestFocus();
            return;
        }

        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"username","password","player_id"},new String[]{userName,userPassword,playerId});
        WebServiceHandler webServiceHandler=new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("LOGIN",response);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    final String status=jsonObject.optString("status");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status.equals("1")){
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_LOGIN,true);
                                JSONObject returnObject=jsonObject.optJSONObject("returnvalue");
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_ID,returnObject.optInt("userid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_NAME,returnObject.optString("username"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_EMAIL,returnObject.optString("userEmail"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_FIRST_NAME,returnObject.optString("userFirstname"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_SUR_NAME,returnObject.optString("userLastname"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_COMPANY_ID,returnObject.optInt("companyid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_NOTIFICATION,returnObject.optBoolean("ReceiveEmailNotifications"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_COUNTRY,returnObject.optString("country"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BDAY,returnObject.optString("bday"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_TITLE,returnObject.optString("businessTitle"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_EMAIL,returnObject.optString("businessEmail"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CELL,returnObject.optString("businessCell"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_TEL,returnObject.optString("businessDirecttel"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_FAX,returnObject.optString("businesscustomfax"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CUSTOMF1,returnObject.optString("businesscustomfield1"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_CUSTOMF2,returnObject.optString("businesscustomfield2"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_STATUS,returnObject.optString("businessstatus"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_EMP_DATE,returnObject.optString("businessempdate"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_PROFESSION,returnObject.optString("businessprofession"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_COMPANY,returnObject.optString("businessCompanyName"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_AUTH_TOKEN,returnObject.optString("authenticationtoken"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_OWN_COMPANY_ID,returnObject.optInt("companyid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_IS_BUSINESS,returnObject.optInt("companyid")==0 ? false : true);

                                AppConstants.AUTH_TOKEN = returnObject.optString("authenticationtoken");
                                String userId=returnObject.optInt("userid")+"";
                                AppConstants.USER_ID=userId;
                                Intent intent;
                                if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO,false))
                                    intent = new Intent(LoginActivity.this,MainActivity_.class);
                                else
                                    intent = new Intent(LoginActivity.this,FlowActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                MyApplication.alertDialog(LoginActivity.this,"Login Failed. Wrong Username or Password","Alert");
                        }
                    });

                }
                catch (JSONException e){

                }

            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_LOGIN,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyAssets();

        //Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());
    }

    private void askPermissions() {
        Ask.on(this)
                .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withRationales("Write Permission is need to be allowed", "Camera Permission is need to be allowed") //optional
                .go();
    }

    //optional
    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void accessFineLocationGranted() {
        isWriteAllowed=true;
    }

    //optional
    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void accessFineLocationDenied() {
        Log.e("Login", "Location  DENiED");
    }

    //optional
    @AskGranted(Manifest.permission.CAMERA)
    public void accessCameraGranted() {
        isCameraAllowed=true;
    }

    //optional
    @AskDenied(Manifest.permission.CAMERA)
    public void accessCameraDenied() {
        Log.e("Login", "Location  DENiED");
    }

    private void copyAssets() {
        String folderName = "avatars";
        final File file = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+folderName);
        if(!file.exists())
            file.mkdir();
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(folderName);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            Log.e("filepath",filename);
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                in = assetManager.open(folderName+"/"+filename);
                File outFile = new File(file, filename);
                fos = new FileOutputStream(outFile);
                    copyFile(in, fos);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        Log.e("ok","ik");
        byte[] buffer = new byte[2018];
        int read=0;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
        in.close();
    }

    private Dialog loadDialog() {
        final Dialog dialogPayment = new Dialog(LoginActivity.this);
        dialogPayment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPayment.setContentView(R.layout.dialog_payment_type);
        dialogPayment.setCancelable(true);

        ImageView imgPayFast = (ImageView) dialogPayment.findViewById(R.id.img_payfast);
        ImageView imgPayPal = (ImageView) dialogPayment.findViewById(R.id.img_paypal);

        imgPayFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPayment.dismiss();
                Intent i = new Intent(LoginActivity.this, SubscriptionActivity_.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
            }
        });

        imgPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPayment.dismiss();
                Intent i = new Intent(LoginActivity.this, SubscriptionPaymentActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
            }
        });

        dialogPayment.show();
        return dialogPayment;
    }

}