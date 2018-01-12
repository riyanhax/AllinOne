package com.parasme.swopinfo.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.alhazmy13.catcho.library.Catcho;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

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

    HashMap<String, String> countryLookupMap = null;

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

        initCountryMap();

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
                        FbLoginActivty.setOnFbClickListener(LoginActivity.this);
                        startActivity(new Intent(LoginActivity.this, FbLoginActivty.class));
                    }
                });

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
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryId = tm.getSimCountryIso();
        String country = countryLookupMap.get(countryId.toUpperCase());

        String id = String.valueOf(object.optLong("id"));
        String email = object.optString("email");
        String first_name = object.optString("name").split(" ")[0];
        String last_name = object.optString("name").split(" ")[1];
        String dob = object.optString("birthday");
        final String picURL = object.optJSONObject("picture").optJSONObject("data").optString("url");

        FormBody.Builder builder = WebServiceHandler.createBuilder(new String[]{"FacebookID", "FirstName", "LastName", "EmailID", "ProfilePicUrl", "DOB", "country", "player_id"},
                new String[]{id, first_name, last_name, email, picURL, dob, country,  playerId});
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("LOGINFB", response);
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

                                if (jsonObject.optString("IsSignUp").equals("1"))
                                    loadFbImage(picURL, userId);
                                else{
                                    Intent intent;
                                    if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO, false))
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    else
                                        intent = new Intent(LoginActivity.this, FlowActivity.class);

                                    startActivity(intent);
                                    finish();
                                }


                            } else
                                MyApplication.alertDialog(LoginActivity.this, jsonObject.optString("message"), "Login Failed");
                        }
                    });

                } catch (JSONException e) {

                }

            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_FB_LOGIN, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadFbImage(String picURL, final String userId) {
        Picasso.with(LoginActivity.this)
                .load(picURL)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        updatePic(bitmap, userId, new File(getCacheDir(), "temp.png").getPath());
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void initCountryMap(){
        countryLookupMap = new HashMap();

        countryLookupMap.put("AD","Andorra");
        countryLookupMap.put("AE","United Arab Emirates");
        countryLookupMap.put("AF","Afghanistan");
        countryLookupMap.put("AG","Antigua and Barbuda");
        countryLookupMap.put("AI","Anguilla");
        countryLookupMap.put("AL","Albania");
        countryLookupMap.put("AM","Armenia");
        countryLookupMap.put("AN","Netherlands Antilles");
        countryLookupMap.put("AO","Angola");
        countryLookupMap.put("AQ","Antarctica");
        countryLookupMap.put("AR","Argentina");
        countryLookupMap.put("AS","American Samoa");
        countryLookupMap.put("AT","Austria");
        countryLookupMap.put("AU","Australia");
        countryLookupMap.put("AW","Aruba");
        countryLookupMap.put("AZ","Azerbaijan");
        countryLookupMap.put("BA","Bosnia and Herzegovina");
        countryLookupMap.put("BB","Barbados");
        countryLookupMap.put("BD","Bangladesh");
        countryLookupMap.put("BE","Belgium");
        countryLookupMap.put("BF","Burkina Faso");
        countryLookupMap.put("BG","Bulgaria");
        countryLookupMap.put("BH","Bahrain");
        countryLookupMap.put("BI","Burundi");
        countryLookupMap.put("BJ","Benin");
        countryLookupMap.put("BM","Bermuda");
        countryLookupMap.put("BN","Brunei");
        countryLookupMap.put("BO","Bolivia");
        countryLookupMap.put("BR","Brazil");
        countryLookupMap.put("BS","Bahamas");
        countryLookupMap.put("BT","Bhutan");
        countryLookupMap.put("BV","Bouvet Island");
        countryLookupMap.put("BW","Botswana");
        countryLookupMap.put("BY","Belarus");
        countryLookupMap.put("BZ","Belize");
        countryLookupMap.put("CA","Canada");
        countryLookupMap.put("CC","Cocos (Keeling) Islands");
        countryLookupMap.put("CD","Congo, The Democratic Republic of the");
        countryLookupMap.put("CF","Central African Republic");
        countryLookupMap.put("CG","Congo");
        countryLookupMap.put("CH","Switzerland");
        countryLookupMap.put("CI","Côte d?Ivoire");
        countryLookupMap.put("CK","Cook Islands");
        countryLookupMap.put("CL","Chile");
        countryLookupMap.put("CM","Cameroon");
        countryLookupMap.put("CN","China");
        countryLookupMap.put("CO","Colombia");
        countryLookupMap.put("CR","Costa Rica");
        countryLookupMap.put("CU","Cuba");
        countryLookupMap.put("CV","Cape Verde");
        countryLookupMap.put("CX","Christmas Island");
        countryLookupMap.put("CY","Cyprus");
        countryLookupMap.put("CZ","Czech Republic");
        countryLookupMap.put("DE","Germany");
        countryLookupMap.put("DJ","Djibouti");
        countryLookupMap.put("DK","Denmark");
        countryLookupMap.put("DM","Dominica");
        countryLookupMap.put("DO","Dominican Republic");
        countryLookupMap.put("DZ","Algeria");
        countryLookupMap.put("EC","Ecuador");
        countryLookupMap.put("EE","Estonia");
        countryLookupMap.put("EG","Egypt");
        countryLookupMap.put("EH","Western Sahara");
        countryLookupMap.put("ER","Eritrea");
        countryLookupMap.put("ES","Spain");
        countryLookupMap.put("ET","Ethiopia");
        countryLookupMap.put("FI","Finland");
        countryLookupMap.put("FJ","Fiji Islands");
        countryLookupMap.put("FK","Falkland Islands");
        countryLookupMap.put("FM","Micronesia, Federated States of");
        countryLookupMap.put("FO","Faroe Islands");
        countryLookupMap.put("FR","France");
        countryLookupMap.put("GA","Gabon");
        countryLookupMap.put("GB","United Kingdom");
        countryLookupMap.put("GD","Grenada");
        countryLookupMap.put("GE","Georgia");
        countryLookupMap.put("GF","French Guiana");
        countryLookupMap.put("GH","Ghana");
        countryLookupMap.put("GI","Gibraltar");
        countryLookupMap.put("GL","Greenland");
        countryLookupMap.put("GM","Gambia");
        countryLookupMap.put("GN","Guinea");
        countryLookupMap.put("GP","Guadeloupe");
        countryLookupMap.put("GQ","Equatorial Guinea");
        countryLookupMap.put("GR","Greece");
        countryLookupMap.put("GS","South Georgia and the South Sandwich Islands");
        countryLookupMap.put("GT","Guatemala");
        countryLookupMap.put("GU","Guam");
        countryLookupMap.put("GW","Guinea-Bissau");
        countryLookupMap.put("GY","Guyana");
        countryLookupMap.put("HK","Hong Kong");
        countryLookupMap.put("HM","Heard Island and McDonald Islands");
        countryLookupMap.put("HN","Honduras");
        countryLookupMap.put("HR","Croatia");
        countryLookupMap.put("HT","Haiti");
        countryLookupMap.put("HU","Hungary");
        countryLookupMap.put("ID","Indonesia");
        countryLookupMap.put("IE","Ireland");
        countryLookupMap.put("IL","Israel");
        countryLookupMap.put("IN","India");
        countryLookupMap.put("IO","British Indian Ocean Territory");
        countryLookupMap.put("IQ","Iraq");
        countryLookupMap.put("IR","Iran");
        countryLookupMap.put("IS","Iceland");
        countryLookupMap.put("IT","Italy");
        countryLookupMap.put("JM","Jamaica");
        countryLookupMap.put("JO","Jordan");
        countryLookupMap.put("JP","Japan");
        countryLookupMap.put("KE","Kenya");
        countryLookupMap.put("KG","Kyrgyzstan");
        countryLookupMap.put("KH","Cambodia");
        countryLookupMap.put("KI","Kiribati");
        countryLookupMap.put("KM","Comoros");
        countryLookupMap.put("KN","Saint Kitts and Nevis");
        countryLookupMap.put("KP","North Korea");
        countryLookupMap.put("KR","South Korea");
        countryLookupMap.put("KW","Kuwait");
        countryLookupMap.put("KY","Cayman Islands");
        countryLookupMap.put("KZ","Kazakstan");
        countryLookupMap.put("LA","Laos");
        countryLookupMap.put("LB","Lebanon");
        countryLookupMap.put("LC","Saint Lucia");
        countryLookupMap.put("LI","Liechtenstein");
        countryLookupMap.put("LK","Sri Lanka");
        countryLookupMap.put("LR","Liberia");
        countryLookupMap.put("LS","Lesotho");
        countryLookupMap.put("LT","Lithuania");
        countryLookupMap.put("LU","Luxembourg");
        countryLookupMap.put("LV","Latvia");
        countryLookupMap.put("LY","Libyan Arab Jamahiriya");
        countryLookupMap.put("MA","Morocco");
        countryLookupMap.put("MC","Monaco");
        countryLookupMap.put("MD","Moldova");
        countryLookupMap.put("MG","Madagascar");
        countryLookupMap.put("MH","Marshall Islands");
        countryLookupMap.put("MK","Macedonia");
        countryLookupMap.put("ML","Mali");
        countryLookupMap.put("MM","Myanmar");
        countryLookupMap.put("MN","Mongolia");
        countryLookupMap.put("MO","Macao");
        countryLookupMap.put("MP","Northern Mariana Islands");
        countryLookupMap.put("MQ","Martinique");
        countryLookupMap.put("MR","Mauritania");
        countryLookupMap.put("MS","Montserrat");
        countryLookupMap.put("MT","Malta");
        countryLookupMap.put("MU","Mauritius");
        countryLookupMap.put("MV","Maldives");
        countryLookupMap.put("MW","Malawi");
        countryLookupMap.put("MX","Mexico");
        countryLookupMap.put("MY","Malaysia");
        countryLookupMap.put("MZ","Mozambique");
        countryLookupMap.put("NA","Namibia");
        countryLookupMap.put("NC","New Caledonia");
        countryLookupMap.put("NE","Niger");
        countryLookupMap.put("NF","Norfolk Island");
        countryLookupMap.put("NG","Nigeria");
        countryLookupMap.put("NI","Nicaragua");
        countryLookupMap.put("NL","Netherlands");
        countryLookupMap.put("NO","Norway");
        countryLookupMap.put("NP","Nepal");
        countryLookupMap.put("NR","Nauru");
        countryLookupMap.put("NU","Niue");
        countryLookupMap.put("NZ","New Zealand");
        countryLookupMap.put("OM","Oman");
        countryLookupMap.put("PA","Panama");
        countryLookupMap.put("PE","Peru");
        countryLookupMap.put("PF","French Polynesia");
        countryLookupMap.put("PG","Papua New Guinea");
        countryLookupMap.put("PH","Philippines");
        countryLookupMap.put("PK","Pakistan");
        countryLookupMap.put("PL","Poland");
        countryLookupMap.put("PM","Saint Pierre and Miquelon");
        countryLookupMap.put("PN","Pitcairn");
        countryLookupMap.put("PR","Puerto Rico");
        countryLookupMap.put("PS","Palestine");
        countryLookupMap.put("PT","Portugal");
        countryLookupMap.put("PW","Palau");
        countryLookupMap.put("PY","Paraguay");
        countryLookupMap.put("QA","Qatar");
        countryLookupMap.put("RE","Réunion");
        countryLookupMap.put("RO","Romania");
        countryLookupMap.put("RU","Russian Federation");
        countryLookupMap.put("RW","Rwanda");
        countryLookupMap.put("SA","Saudi Arabia");
        countryLookupMap.put("SB","Solomon Islands");
        countryLookupMap.put("SC","Seychelles");
        countryLookupMap.put("SD","Sudan");
        countryLookupMap.put("SE","Sweden");
        countryLookupMap.put("SG","Singapore");
        countryLookupMap.put("SH","Saint Helena");
        countryLookupMap.put("SI","Slovenia");
        countryLookupMap.put("SJ","Svalbard and Jan Mayen");
        countryLookupMap.put("SK","Slovakia");
        countryLookupMap.put("SL","Sierra Leone");
        countryLookupMap.put("SM","San Marino");
        countryLookupMap.put("SN","Senegal");
        countryLookupMap.put("SO","Somalia");
        countryLookupMap.put("SR","Suriname");
        countryLookupMap.put("ST","Sao Tome and Principe");
        countryLookupMap.put("SV","El Salvador");
        countryLookupMap.put("SY","Syria");
        countryLookupMap.put("SZ","Swaziland");
        countryLookupMap.put("TC","Turks and Caicos Islands");
        countryLookupMap.put("TD","Chad");
        countryLookupMap.put("TF","French Southern territories");
        countryLookupMap.put("TG","Togo");
        countryLookupMap.put("TH","Thailand");
        countryLookupMap.put("TJ","Tajikistan");
        countryLookupMap.put("TK","Tokelau");
        countryLookupMap.put("TM","Turkmenistan");
        countryLookupMap.put("TN","Tunisia");
        countryLookupMap.put("TO","Tonga");
        countryLookupMap.put("TP","East Timor");
        countryLookupMap.put("TR","Turkey");
        countryLookupMap.put("TT","Trinidad and Tobago");
        countryLookupMap.put("TV","Tuvalu");
        countryLookupMap.put("TW","Taiwan");
        countryLookupMap.put("TZ","Tanzania");
        countryLookupMap.put("UA","Ukraine");
        countryLookupMap.put("UG","Uganda");
        countryLookupMap.put("UM","United States Minor Outlying Islands");
        countryLookupMap.put("US","United States");
        countryLookupMap.put("UY","Uruguay");
        countryLookupMap.put("UZ","Uzbekistan");
        countryLookupMap.put("VA","Holy See (Vatican City State)");
        countryLookupMap.put("VC","Saint Vincent and the Grenadines");
        countryLookupMap.put("VE","Venezuela");
        countryLookupMap.put("VG","Virgin Islands, British");
        countryLookupMap.put("VI","Virgin Islands, U.S.");
        countryLookupMap.put("VN","Vietnam");
        countryLookupMap.put("VU","Vanuatu");
        countryLookupMap.put("WF","Wallis and Futuna");
        countryLookupMap.put("WS","Samoa");
        countryLookupMap.put("YE","Yemen");
        countryLookupMap.put("YT","Mayotte");
        countryLookupMap.put("YU","Yugoslavia");
        countryLookupMap.put("ZA","South Africa");
        countryLookupMap.put("ZM","Zambia");
        countryLookupMap.put("ZW","Zimbabwe");
    }

    private void updatePic(final Bitmap bitmap, String userId, final String path) {
        try {
            File f = new File(getCacheDir(), "temp.png");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        catch (Exception e){e.printStackTrace();}

        WebServiceHandler webServiceHandler=new WebServiceHandler(LoginActivity.this);
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
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                else
                                    intent = new Intent(LoginActivity.this, FlowActivity.class);

                                startActivity(intent);
                                finish();

                            }
                            else
                                MyApplication.alertDialog(LoginActivity.this,"Could not register","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_USER +"profilepic/" + userId , WebServiceHandler.createMultiPartBuilder(new String[]{"profilepic"},new String[]{path}),userId);
        }catch (IOException e){}
    }

}