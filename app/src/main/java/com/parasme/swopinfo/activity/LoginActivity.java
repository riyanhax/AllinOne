package com.parasme.swopinfo.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentSubscription;
import com.parasme.swopinfo.fragment.FragmentSubscriptionPayment;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import net.alhazmy13.catcho.library.Catcho;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Text;

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

public class LoginActivity extends AppCompatActivity implements FbLoginActivty.FbSignInDetail {

    private boolean isWriteAllowed = false;
    private boolean isCameraAllowed = false;
    private boolean isLocationAllowed = false;
    private WebView webView;
    private ProgressBar progressBar;
    private boolean isURLShown = false;
    private String playerId = "";

    EditText editUserName, editPassword;
    TextView textForgot, textCreateAccount;
    Button btnLogin;
    private final int MY_PERMISSIONS_REQUEST_CAMERA_WRITE_LOCATION=111;
    private RelativeLayout layoutFbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());

        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassword = (EditText) findViewById(R.id.editPassword);

        editUserName.setFilters(new InputFilter[]{MyApplication.EMOJI_FILTER});
        editPassword.setFilters(new InputFilter[]{MyApplication.EMOJI_FILTER});

        textForgot = (TextView) findViewById(R.id.textForgot);
        textCreateAccount = (TextView) findViewById(R.id.textCreateAccount);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        layoutFbLogin = (RelativeLayout) findViewById(R.id.layout_fb_login);

        copyAssets();


        textForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog registerDialog = Utils.loadRegisterDialog(LoginActivity.this);
                Button btnIndividual = (Button) registerDialog.findViewById(R.id.btnIndividual);
                Button btnBusiness = (Button) registerDialog.findViewById(R.id.btnBusiness);

                btnIndividual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
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
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                String emoji = editUserName.getText().toString();
                String string1 = "";
                string1 = EmojiHandler.encodeJava(emoji);
                Log.i("tag1", ""+string1);

                string1 = EmojiHandler.decodeJava(string1);
                Log.i("tag2", ""+string1);

                btnLogin.setText(string1);
*/

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((ActivityCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)

                            || (ActivityCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)

                            || (ActivityCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)) {

                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_CAMERA_WRITE_LOCATION);

                        // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    } else {
                        validateAndLogin();
                    }
                }
                else
                    validateAndLogin();

            }
        });

        layoutFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FbLoginActivty.setOnFbClickListener(LoginActivity.this);
                startActivity(new Intent(LoginActivity.this, FbLoginActivty.class));
            }
        });

        //new VersionChecker().execute();
    }






    private void validateAndLogin() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                String text = "OneSignal UserID:\n" + userId + "\n\n";
                playerId = userId;
                Log.e("ONESIN", "idsAvailable: " + userId + "____" + registrationId);
                if (!userId.equals(""))
                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_PLAYER_ID, userId);
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_GCM_TOKEN, registrationId);
                if (registrationId != null)
                    text += "Google Registration Id:\n" + registrationId;
                else
                    text += "Google Registration Id:\nCould not subscribe for push";
            }
        });

        String userName = editUserName.getText().toString();
        String userPassword = editPassword.getText().toString();

        if (userName.equals("")) {
            editUserName.setError("Field cannot be empty");
            editUserName.requestFocus();
            return;
        }

        if (userPassword.equals("")) {
            editPassword.setError("Field cannot be empty");
            editPassword.requestFocus();
            return;
        }

        FormBody.Builder builder = WebServiceHandler.createBuilder(new String[]{"username", "password", "player_id"}, new String[]{userName, userPassword, playerId});
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("LOGIN", response);
                try {
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
                                Intent intent;
                                if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO, false))
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                else
                                    intent = new Intent(LoginActivity.this, FlowActivity.class);
                                startActivity(intent);
                                finish();
                            } else
                                MyApplication.alertDialog(LoginActivity.this, "Login Failed. Wrong Username or Password", "Alert");
                        }
                    });

                } catch (JSONException e) {

                }

            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_LOGIN, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void copyAssets() {
        String folderName = "avatars";
        final File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + folderName);
        if (!file.exists())
            file.mkdir();
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(folderName);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            Log.e("filepath", filename);
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                in = assetManager.open(folderName + "/" + filename);
                File outFile = new File(file, filename);
                fos = new FileOutputStream(outFile);
                copyFile(in, fos);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
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
        Log.e("ok", "ik");
        byte[] buffer = new byte[2018];
        int read = 0;
        while ((read = in.read(buffer)) != -1) {
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
                Intent i = new Intent(LoginActivity.this, SubscriptionActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
            }
        });

        imgPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPayment.dismiss();
                Intent i = new Intent(LoginActivity.this, SubscriptionPayPalActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
            }
        });

        dialogPayment.show();
        return dialogPayment;
    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.parasme.swopinfo" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("CCCCCVER",s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAnyPermissionDenied = false;
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_WRITE_LOCATION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    isAnyPermissionDenied = true;
                    break;
                }
            }
            if (!isAnyPermissionDenied) {
                validateAndLogin();
            }
        }
    }


    @Override
    public void onGetFBAccountDetail(final JSONObject object) {
        String id = String.valueOf(object.optLong("id"));
        String email = object.optString("email");
        String first_name = object.optString("first_name");
        String last_name = object.optString("last_name");

        FormBody.Builder builder = WebServiceHandler.createBuilder(new String[]{"username", "password", "player_id"}, new String[]{"mukeshkmwt", "parasme", playerId});
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("LOGIN", response);
                try {
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
                                Intent intent;
                                if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO, false))
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                else
                                    intent = new Intent(LoginActivity.this, FlowActivity.class);
                                startActivity(intent);
                                finish();
                            } else
                                MyApplication.alertDialog(LoginActivity.this, "Login Failed. Wrong Username or Password", "Alert");
                        }
                    });

                } catch (JSONException e) {

                }

            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_LOGIN, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}