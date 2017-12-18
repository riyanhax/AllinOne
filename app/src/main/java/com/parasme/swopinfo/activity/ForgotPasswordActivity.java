package com.parasme.swopinfo.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentUser;
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

public class ForgotPasswordActivity extends AppCompatActivity {



    EditText editUserName;
    TextView textCreateAccount, textLogin;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());

        editUserName = (EditText) findViewById(R.id.editUserName);
        textCreateAccount = (TextView) findViewById(R.id.textCreateAccount);
        textLogin = (TextView) findViewById(R.id.textLogin);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog registerDialog = Utils.loadRegisterDialog(ForgotPasswordActivity.this);
                Button btnIndividual = (Button) registerDialog.findViewById(R.id.btnIndividual);
                Button btnBusiness = (Button) registerDialog.findViewById(R.id.btnBusiness);

                btnIndividual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDialog.dismiss();
                        Intent i = new Intent(ForgotPasswordActivity.this, SignUpActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                    }
                });

                btnBusiness.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDialog.dismiss();
                        Intent i = new Intent(ForgotPasswordActivity.this, SubscriptionActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                    }
                });
                registerDialog.show();

            }
        });

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                finish();
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
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        String userName=editUserName.getText().toString();

        if(userName.equals("")){
            editUserName.setError("Field cannot be empty");
            editUserName.requestFocus();
            return;
        }


        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"userEmail"},new String[]{userName});
        WebServiceHandler webServiceHandler=new WebServiceHandler(ForgotPasswordActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("FORGOT",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("200"))
                            MyApplication.alertDialog(ForgotPasswordActivity.this,"Reset link is sent to email address","Reset Password");
                        else if(response.contains("901"))
                            MyApplication.alertDialog(ForgotPasswordActivity.this,"No user found with this username","Wrong Username");
                        else
                            MyApplication.alertDialog(ForgotPasswordActivity.this,"Could not process the request","Reset Failure");
                    }
                });
            }
        };

        try {
            webServiceHandler.post(AppConstants.URL_FORGOT,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}