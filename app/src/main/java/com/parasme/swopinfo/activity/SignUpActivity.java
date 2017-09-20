package com.parasme.swopinfo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import net.alhazmy13.catcho.library.Catcho;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

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

@EActivity(R.layout.activity_signup)
public class SignUpActivity extends AppCompatActivity implements ImagePicker.Picker{

    private ArrayList<String> countryNameList = new ArrayList<>();
    private Dialog dialogCountry;
    private ListView listViewCountry;
    private boolean isFirstPhase=true;
    private View rootView;
    private ArrayList<EditText> editTextArrayList1 =new ArrayList<>();
    private ArrayList<EditText> editTextArrayList2 =new ArrayList<>();
    private String imagePath="";

    @ViewById EditText editCountry;
    @ViewById EditText editEmail;
    @ViewById EditText editFirstName;
    @ViewById EditText editLastName;
    @ViewById EditText editUserName;
    @ViewById EditText editPassword;
    @ViewById EditText editConfirmPassword;
    @ViewById EditText editDOB;
    @ViewById LinearLayout layoutPhase1;
    @ViewById LinearLayout layoutPhase2;
    @ViewById ImageView imageUser;
    @ViewById ImageView imageUser2;


    @Click(R.id.editDOB)
    void clickDOB(){
        new Utils(SignUpActivity.this).selectDate(editDOB);
    }

    @Click(R.id.btnNext)
    void click(){
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

    @Click(R.id.btnSubmit)
    void submitClick(){
        validationAndRegister();
    }


    @Click(R.id.editCountry)
    void countryClick(){
        dialogCountry.show();
    }

    @Click(R.id.imageUser)
    void imageClick(){
        ImagePicker.picker=this;
        startActivity(new Intent(SignUpActivity.this,ImagePicker.class).putExtra("avatar","avatar"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());
    }

    @AfterViews
    protected void init(){
        editTextArrayList1.add(editEmail);
        editTextArrayList1.add(editFirstName);
        editTextArrayList1.add(editLastName);
        editTextArrayList1.add(editUserName);

        editTextArrayList2.add(editDOB);
        editTextArrayList2.add(editCountry);
        editTextArrayList2.add(editPassword);
        editTextArrayList2.add(editConfirmPassword);

        countryNameList= new Utils(SignUpActivity.this).getCountryNames();
        loadCountryDialog();
        rootView=this.getWindow().getDecorView().findViewById(android.R.id.content);
    }


    public void loadCountryDialog() {

        Object[] objects=new Utils(SignUpActivity.this).loadCountryDialog(countryNameList);
        dialogCountry= (Dialog) objects[0];
        listViewCountry= (ListView) objects[1];

        listViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        if(!editTextArrayList2.get(2).getText().toString().equals(editTextArrayList2.get(3).getText().toString())){
            editTextArrayList2.get(2).setError("Please enter same passwords");
            editTextArrayList2.get(2).requestFocus();
            return;
        }
        else{
            editTextArrayList2.get(2).clearFocus();
            editTextArrayList2.get(2).setError(null);
        }

        String paramsArray[]={"userid","username","userEmail","userFirstname","userLastname","companyid","ReceiveEmailNotifications","country","bday","password"};
        String valuesArray[]={"0",editUserName.getText().toString(),editEmail.getText().toString(),editFirstName.getText().toString(),editLastName.getText().toString(),"0","true",editCountry.getText().toString(),editDOB.getText().toString(),editPassword.getText().toString()};
//        String paramsArray[]={"userid","username","userEmail","userFirstname","userLastname","companyid","ReceiveEmailNotifications","country","bday","password","profilepic"};
//        String valuesArray[]={"0",editUserName.getText().toString(),editEmail.getText().toString(),editFirstName.getText().toString(),editLastName.getText().toString(),"0","true",editCountry.getText().toString(),editDOB.getText().toString(),editPassword.getText().toString(),imagePath};

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
                                try {
                                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_AUTH_TOKEN,"abcd1234");
                                    updatePic(null,imagePath,jsonObject.getJSONObject("returnvalue").getInt("userid"));
                                } catch (JSONException e) {e.printStackTrace();}
                            }
                            else if(status.equals("991"))
                                MyApplication.alertDialog(SignUpActivity.this,"Email Already Exists","Alert");
                            else if(status.equals("992"))
                                MyApplication.alertDialog(SignUpActivity.this,"Username Already Exists","Alert");
                        }
                    });

                }
                catch (JSONException e){

                }
            }
        };

        try {
            webServiceHandler.post(AppConstants.URL_REGISTER,builder);
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

    private void updatePic(final Bitmap bitmap, String imagePath, int userId) {
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