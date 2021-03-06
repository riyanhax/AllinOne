package com.parasme.swopinfo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import net.alhazmy13.catcho.library.Catcho;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MultipartBody;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class SignUpActivity extends AppCompatActivity implements ImagePicker.Picker{

    private ArrayList<String> countryNameList = new ArrayList<>();
    private ArrayList<String> countryCodeList = new ArrayList<>();
    private Dialog dialogCountry;
    private ListView listViewCountry;
    private boolean isFirstPhase=true;
    private View rootView;
    private ArrayList<EditText> editTextArrayList1 =new ArrayList<>();
    private ArrayList<EditText> editTextArrayList2 =new ArrayList<>();
    public static String imagePath="";

    EditText editCountry,editEmail,editFirstName,editLastName,editUserName,editPassword,
            editConfirmPassword,editDOB,editMobile,editCountryCode;
    LinearLayout layoutPhase1,layoutPhase2;
    ImageView imageUser,imageUser2;
    Button btnNext, btnSubmit;
    private String userTempId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());

        editCountry = (EditText) findViewById(R.id.editCountry);
        editCountryCode = (EditText) findViewById(R.id.editCountryCode);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        editDOB = (EditText) findViewById(R.id.editDOB);
        editMobile = (EditText) findViewById(R.id.editMobile);

        layoutPhase1 = (LinearLayout) findViewById(R.id.layoutPhase1);
        layoutPhase2 = (LinearLayout) findViewById(R.id.layoutPhase2);

        imageUser = (ImageView) findViewById(R.id.imageUser);
        imageUser2 = (ImageView) findViewById(R.id.imageUser2);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        init();

        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils(SignUpActivity.this).selectDate(editDOB);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.toggleSoftKeyBoard(SignUpActivity.this,true);
                for(int i=0;i<editTextArrayList1.size();i++) {
                    EditText editText = editTextArrayList1.get(i);
                    if(editText.getText().toString().equals("")){
                        editText.setError("Field can not be empty");
                        editText.requestFocus();
                        return;
                    }
                    else{
                        editText.clearFocus();
                        editText.setError(null);
                    }
                }

                EditText editTextEmail=editTextArrayList1.get(0);
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
                    editTextEmail.setError("Please enter a valid email address");
                    editTextEmail.requestFocus();
                    return;
                }

                if(imagePath.equals("")){
                    Snackbar.make(findViewById(android.R.id.content),"Please select an profile picture first",Snackbar.LENGTH_LONG).show();
                    return;
                }

                inflateSignUpPhase();
                isFirstPhase=false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationAndRegister();
            }
        });

        editCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCountry.show();
            }
        });

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.picker=SignUpActivity.this;
                startActivity(new Intent(SignUpActivity.this,ImagePicker.class).putExtra("avatar","avatar"));
            }
        });
    }


    protected void init(){
        editTextArrayList1.add(editEmail);
        editTextArrayList1.add(editFirstName);
        editTextArrayList1.add(editLastName);
        editTextArrayList1.add(editUserName);
        editTextArrayList1.add(editDOB);

        editTextArrayList2.add(editCountry);
        editTextArrayList2.add(editPassword);
        editTextArrayList2.add(editConfirmPassword);
        editTextArrayList2.add(editMobile);

        countryNameList= new Utils(SignUpActivity.this).getCountryNames();
        countryCodeList= new Utils(SignUpActivity.this).getCountryCodes();
        loadCountryDialog();
        rootView=this.getWindow().getDecorView().findViewById(android.R.id.content);

        editMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if (string.startsWith("0"))
                    editMobile.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setCountryAndCode();

    }

    private void setCountryAndCode() {
        String countryCode="", countryName="";
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryId = tm.getSimCountryIso().toUpperCase();
        if (!StringUtil.isBlank(countryId)) {
            String[] countries = getResources().getStringArray(R.array.countries);
            for (int i = 0; i < countries.length; i++) {
                Log.e("pos", i + "__" + countries.length);
                String id = countries[i].split(",")[1];
                if (countryId.equalsIgnoreCase(id)) {
                    countryCode = countries[i].split(",")[0];
                    countryName = countries[i].split(",")[2];
                    break;
                }
            }
            editCountry.setText(countryName);
            editCountryCode.setText("+"+countryCode);
        }

    }


    public void loadCountryDialog() {

        Object[] objects=new Utils(SignUpActivity.this).loadCountryDialog(countryNameList);
        dialogCountry= (Dialog) objects[0];
        listViewCountry= (ListView) objects[1];

        listViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editCountryCode.setText("+"+countryCodeList.get(position));
                editCountry.setText(countryNameList.get(position));
                dialogCountry.dismiss();
            }
        });
    }

    private ArrayAdapter<String> setAdapter(final ArrayList<String> arrayList){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_list_item_1, arrayList){

            @Override
            public View getView(final int position, View convertView, ViewGroup paren) {
                View view = super.getView(position, convertView, paren);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };

        return adapter;
    }

    private void inflateSignUpPhase() {

        rootView.startAnimation(AnimationUtils.loadAnimation(this, isFirstPhase ? R.anim.anim_slide_out_left : R.anim.anim_slide_in_right));

        layoutPhase1.setVisibility(isFirstPhase ? View.GONE : View.VISIBLE);
        layoutPhase2.setVisibility(isFirstPhase ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onBackPressed() {
        if(isFirstPhase)
            finish();
        else{
            inflateSignUpPhase();
            isFirstPhase=true;
        }
    }

    private void validationAndRegister() {
        for(int i = 0; i< editTextArrayList2.size(); i++){
            EditText editText= editTextArrayList2.get(i);
            if(editText.getText().toString().equals("")){
                editText.setError("Field can not be empty");
                editText.requestFocus();
                return;
            }
            else{
                editText.clearFocus();
                editText.setError(null);
            }
        }

        if(!editTextArrayList2.get(1).getText().toString().equals(editTextArrayList2.get(2).getText().toString())){
            editTextArrayList2.get(1).setError("Please enter same passwords");
            editTextArrayList2.get(1).requestFocus();
            return;
        }
        else{
            editTextArrayList2.get(1).clearFocus();
            editTextArrayList2.get(1).setError(null);
        }
        String mob = editCountryCode.getText().toString()+editMobile.getText().toString();

        // Encoding emojis if exists
        String firstName = EmojiHandler.encodeJava(editFirstName.getText().toString());
        String lastName = EmojiHandler.encodeJava(editLastName.getText().toString());

        String paramsArray[]={"userid","username","userEmail","userFirstname","userLastname","companyid","ReceiveEmailNotifications","country","bday","password","userMobile"};
        String valuesArray[]={"0",editUserName.getText().toString(),editEmail.getText().toString(),firstName,lastName,"0","true",editCountry.getText().toString(),editDOB.getText().toString(),editPassword.getText().toString(),mob};
//        String paramsArray[]={"userid","username","userEmail","userFirstname","userLastname","companyid","ReceiveEmailNotifications","country","bday","password","profilepic"};
//        String valuesArray[]={"0",editUserName.getText().toString(),editEmail.getText().toString(),editFirstName.getText().toString(),editLastName.getText().toString(),"0","true",editCountry.getText().toString(),editDOB.getText().toString(),editPassword.getText().toString(),imagePath};

        // Creating url according to country
        final String country = editCountry.getText().toString();
        String url = AppConstants.URL_REGISTER_WITHOUT_OTP;
        if (country.equalsIgnoreCase("south africa"))
            url = AppConstants.URL_REGISTER;


        FormBody.Builder builder= WebServiceHandler.createBuilder(paramsArray,valuesArray);
        WebServiceHandler webServiceHandler=new WebServiceHandler(SignUpActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("SIGNUP",response);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    final String status=jsonObject.optString("status");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status.equals("1")){
                                //MyApplication.alertDialog(SignUpActivity.this,"Verify mail is sent to your email id!","Alert");
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_AUTH_TOKEN,"abcd1234");
                                try {
                                    if (country.equalsIgnoreCase("south africa")){
                                        userTempId = jsonObject.getString("returnvalue");
                                        Intent intent = new Intent(SignUpActivity.this, OTPActivity.class);
                                        intent.putExtra("id",jsonObject.getString("returnvalue"));
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                        updatePic(null,imagePath,jsonObject.getJSONObject("returnvalue").getInt("userid"), editCountry.getText().toString());

                                } catch (JSONException e) {e.printStackTrace();}
                            }
                            else if(status.equals("991"))
                                MyApplication.alertDialog(SignUpActivity.this,"Email Already Exists","Alert");
                            else if(status.equals("992"))
                                MyApplication.alertDialog(SignUpActivity.this,"Username Already Exists","Alert");
                            else
                                MyApplication.alertDialog(SignUpActivity.this, jsonObject.optString("message"),"Alert");
                        }
                    });
                }
                catch (JSONException e){

                }
            }
        };

        try {
            webServiceHandler.post(url,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onImagePicked(Bitmap bitmap, String imagePath) {
        imageUser.setImageBitmap(bitmap);
        imageUser2.setImageBitmap(bitmap);
        this.imagePath = imagePath;
    }

    @Override
    public void onVideoPicked(String videoPath) {

    }

    private void updatePic(final Bitmap bitmap, String imagePath, int userId, final String country) {
        WebServiceHandler webServiceHandler=new WebServiceHandler(SignUpActivity.this);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Pictt Resp",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                    MyApplication.alertDialog(SignUpActivity.this,"Verify mail is sent to your email id","Alert");
                            }
                            else
                                MyApplication.alertDialog(SignUpActivity.this,"Could not register","Alert");

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