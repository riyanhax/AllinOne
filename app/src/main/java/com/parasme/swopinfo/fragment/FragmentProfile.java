package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LoginActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.FormBody;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentProfile extends Fragment implements ImagePicker.Picker, View.OnClickListener {

    private ImageView imageUser;
    private EditText editProfileEmail;
    private EditText editProfileFName;
    private EditText editProfileLName;
    private CheckBox checkNotification;
    private EditText editProfileDOB;
    private EditText editProfileLCountry;
    private EditText editCompanyName;
    private EditText editCompanyTitle;
    private EditText editCompanyEmail;
    private EditText editStartDate;
    private EditText editProfession;
    private EditText editCell;
    private EditText editDirectTell;
    private EditText editFax;
    private EditText editCustomField1;
    private EditText editCustomField2;
    private ArrayList<EditText> editTextArrayList=new ArrayList<>();
    private ArrayList<String> countryNameList = new ArrayList<>();
    private ArrayList<String> professionList = new ArrayList<>();
    private Dialog dialogCountry;
    private Dialog dialogProfession;
    private ListView listViewCountry;
    private ListView listViewProfession;
    private boolean isFirstLoad=true;
    private int professionId=6;
    private AppCompatActivity mActivity;
    private final String TAG  = this.getClass().getName();
    private TextView textProfileViews,textUserFullName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageUser = (ImageView) view.findViewById(R.id.imageUser);
        textProfileViews = (TextView) view.findViewById(R.id.textProfileViews);
        textUserFullName = (TextView) view.findViewById(R.id.textUserFullName);
        editProfileEmail = (EditText) view.findViewById(R.id.editProfileEmail);
        editProfileFName = (EditText) view.findViewById(R.id.editProfileFName);
        editProfileLName = (EditText) view.findViewById(R.id.editProfileLName);
        checkNotification = (CheckBox) view.findViewById(R.id.checkNotification);
        editProfileDOB = (EditText) view.findViewById(R.id.editProfileDOB);
        editProfileLCountry = (EditText) view.findViewById(R.id.editProfileLCountry);
        editCompanyName = (EditText) view.findViewById(R.id.editCompanyName);
        editCompanyTitle = (EditText) view.findViewById(R.id.editCompanyTitle);
        editCompanyEmail = (EditText) view.findViewById(R.id.editCompanyEmail);
        editStartDate = (EditText) view.findViewById(R.id.editStartDate);
        editProfession = (EditText) view.findViewById(R.id.editProfession);
        editCell = (EditText) view.findViewById(R.id.editCell);
        editDirectTell = (EditText) view.findViewById(R.id.editDirectTell);
        editFax = (EditText) view.findViewById(R.id.editFax);
        editCustomField1 = (EditText) view.findViewById(R.id.editCustomField1);
        editCustomField2 = (EditText) view.findViewById(R.id.editCustomField2);

        ((Button) view.findViewById(R.id.btnUpdateProfle)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnDeleteProfle)).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.editProfileLCountry)).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.editProfileDOB)).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.editProfession)).setOnClickListener(this);
        ((ImageView) view.findViewById(R.id.imageUser)).setOnClickListener(this);

        String imageURL=AppConstants.URL_DOMAIN+"upload/user"+ AppConstants.USER_ID+"/profilepic.jpg";
        Picasso.with(mActivity).load(imageURL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(imageUser);


        editTextArrayList.add(editProfileEmail);
        editTextArrayList.add(editProfileFName);
        editTextArrayList.add(editProfileLName);
        editTextArrayList.add(editProfileDOB);
        editTextArrayList.add(editProfileLCountry);
        editTextArrayList.add(editCompanyName);
        editTextArrayList.add(editCompanyTitle);
        editTextArrayList.add(editCompanyEmail);
        editTextArrayList.add(editStartDate);
        editTextArrayList.add(editProfession);
        editTextArrayList.add(editCell);
        editTextArrayList.add(editDirectTell);
        editTextArrayList.add(editFax);
        editTextArrayList.add(editCustomField1);
        editTextArrayList.add(editCustomField2);

        countryNameList= new Utils(mActivity).getCountryNames();
        loadCountryDialog();
        loadProfessionDialog();

        setFields();



        return view;
    }

    private void getProfessions() {
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Professions",response);
                try{
                    String savedProfId=(String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_PROFESSION);

                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        final JSONObject professionObject=jsonArray.getJSONObject(i);

                        if(savedProfId.equals(professionObject.optInt("profid")+""))
                            if(!mActivity.isFinishing()) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editProfession.setText(professionObject.optString("profession"));
                                    }
                                });
                            }
                        professionList.add(professionObject.optInt("profid")+":"+professionObject.optString("profession"));
                    }

                }catch (JSONException e){}
            }
        };
        try {
            webServiceHandler.get(AppConstants.URL_PROFESSIONS);
        }catch (IOException e){}
    }


    private void validateAndUpdate() {
        String userId=AppConstants.USER_ID;

        String stringProfileEmail = editProfileEmail.getText().toString();
        String stringProfileFName = EmojiHandler.encodeJava(editProfileFName.getText().toString());
        String stringProfileLName = EmojiHandler.encodeJava(editProfileLName.getText().toString());

        String stringCheckNotification = checkNotification.isChecked() ? "true" : "false";
        String stringProfileDOB = editProfileDOB.getText().toString();
        String stringProfileLCountry = editProfileLCountry.getText().toString();
        String stringCompanyName = editCompanyName.getText().toString();
        String stringCompanyTitle = editCompanyTitle.getText().toString();
        String stringCompanyEmail = editCompanyEmail.getText().toString();
        String stringStartDate = editStartDate.getText().toString();
        String stringProfession = String.valueOf(professionId);
        String stringCell = editCell.getText().toString();
        String stringDirectTell = editDirectTell.getText().toString();
        String stringFax = editFax.getText().toString();
        String stringCustomField1 = editCustomField1.getText().toString();
        String stringCustomField2 = editCustomField2.getText().toString();

        String paramsArray[]={"userid","userEmail","userFirstname","userLastname","companyid","ReceiveEmailNotifications","country","bday","businessTitle","businessEmail","businessCell","businessDirecttel","businesscustomfax","businesscustomfield1","businesscustomfield2","businessstatus","businessempdate","businessprofession","businessCompanyName"};
        String valuesArray[]={userId,stringProfileEmail,stringProfileFName,stringProfileLName,"0",stringCheckNotification,stringProfileLCountry,stringProfileDOB,stringCompanyTitle,stringCompanyEmail,stringCell,stringDirectTell,stringFax,stringCustomField1,stringCustomField2,stringCompanyTitle,stringStartDate,stringProfession,stringCompanyName};

        FormBody.Builder builder= WebServiceHandler.createBuilder(paramsArray,valuesArray);
        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Update Resp",response);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    final String status=jsonObject.optString("status");

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status.equals("1")){
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_LOGIN,true);
                                JSONObject returnObject=jsonObject.optJSONObject("returnvalue");
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_ID,returnObject.optInt("userid"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_NAME,returnObject.optString("username"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_EMAIL,returnObject.optString("userEmail"));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_FIRST_NAME,EmojiHandler.decodeJava(returnObject.optString("userFirstname")));
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_USER_SUR_NAME,EmojiHandler.decodeJava(returnObject.optString("userLastname")));
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
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_PROFESSION,returnObject.optInt("businessprofession")+"");
                                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_BUSINESS_COMPANY,returnObject.optString("businessCompanyName"));
                                textUserFullName.setText(editProfileFName.getText().toString()+" "+editProfileLName.getText().toString());
                                MyApplication.alertDialog(mActivity,"Profile Successfully Updated","Update Success");
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could Not Update","Alert");
                        }
                    });

                }
                catch (JSONException e){

                }

            }
        };

        try {
            webServiceHandler.post(AppConstants.URL_UPDATE, builder);
        }catch (IOException e){}
    }

    private void setFields() {
        textProfileViews.setText("Profile Views: "+AppConstants.PROFILE_VIEWS);
        String lastName = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME);
        textUserFullName.setText(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME) +" " + lastName);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_EMAIL),editProfileEmail);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME),editProfileFName);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME),editProfileLName);

        if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_NOTIFICATION,false))
            checkNotification.setChecked(true);
        else
            checkNotification.setChecked(false);

        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BDAY),editProfileDOB);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COUNTRY),editProfileLCountry);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_COMPANY),editCompanyName);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_TITLE),editCompanyTitle);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_EMAIL),editCompanyEmail);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_EMP_DATE),editStartDate);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_CELL),editCell);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_TEL),editDirectTell);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_FAX),editFax);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_CUSTOMF1),editCustomField1);
        setEditField((String) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_BUSINESS_CUSTOMF2),editCustomField2);

    }

    private void setEditField(String value, EditText editText) {
        if(value.equals(null) || value==null)
            editText.setText("");
        else
            editText.setText(value);
    }

    public void loadCountryDialog() {

        Object[] objects=new Utils(mActivity).loadCountryDialog(countryNameList);
        dialogCountry= (Dialog) objects[0];
        listViewCountry= (ListView) objects[1];

        listViewCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editProfileLCountry.setText(countryNameList.get(position));
                dialogCountry.dismiss();
            }
        });
    }
    public void loadProfessionDialog() {
        Object[] objects=new Utils(mActivity).loadCountryDialog(professionList);
        dialogProfession= (Dialog) objects[0];
        listViewProfession= (ListView) objects[1];

        listViewProfession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String [] profession=professionList.get(position).split(":");
                professionId = Integer.parseInt(profession[0]);
                String professionName=profession[1];
                editProfession.setText(professionName);
                Log.e("PROFESSIONiD",professionId+"");
                dialogProfession.dismiss();
            }
        });
    }


    @Override
    public void onImagePicked(final Bitmap bitmap, final String imagePath) {
        Log.e("FINALIMAGEPATH",imagePath);
        AlertDialog.Builder adb = new AlertDialog.Builder(mActivity);
        adb.setMessage("Update profile picture with selected image ?");
        adb.setTitle("Update Profile");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePic(bitmap,imagePath);
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    @Override
    public void onVideoPicked(String videoPath) {

    }

    private void updatePic(final Bitmap bitmap, String imagePath) {
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Pictt Resp",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                MyApplication.alertDialog(mActivity,"Profile picture updated successfully","Update Picture");
                                imageUser.setImageBitmap(bitmap);
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not update profile picture","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_UPDATE_PROFILE_PIC + AppConstants.USER_ID , WebServiceHandler.createMultiPartBuilder(new String[]{"profilepic"},new String[]{imagePath}),AppConstants.USER_ID);
        }catch (IOException e){}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Profile");

        if(isFirstLoad){
            professionList.clear();
            getProfessions();
        }
        isFirstLoad=false;

    }

/*
    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
*/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUpdateProfle:
                validateAndUpdate();
                break;
            case R.id.editProfileLCountry:
                dialogCountry.show();
                break;
            case R.id.editProfileDOB:
                new Utils(mActivity).selectDate(editProfileDOB);
                break;
            case R.id.editStartDate:
                new Utils(mActivity).selectDate(editStartDate);
                break;
            case R.id.editProfession:
                dialogProfession.show();
                break;
            case R.id.imageUser:
                ImagePicker.picker=this;
                startActivity(new Intent(mActivity,ImagePicker.class).putExtra("avatar","avatar"));
                break;
            case R.id.btnDeleteProfle:
                showDeleteDialog();
                break;
        }
    }

//    {"Code":"200","Status":"SUCCESS"}
    private void showDeleteDialog() {
        new android.app.AlertDialog.Builder(mActivity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete entire profile?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfile(AppConstants.USER_ID);
                    }
                    //dialog.dismiss();

                }).setNegativeButton("No", null).show();

    }

    private void deleteProfile(String userId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e(TAG, "onResponse: "+ response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(new JSONObject(response).optString("Code").equals("200")){
                                SharedPreferenceUtility.getInstance().clearSharedPreferences();
                                // replaceFragment(new FragmentHome_(),getFragmentManager());
                                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                mActivity.finish();
                            }
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_DELETE_PROFILE + userId +"/delete",WebServiceHandler.createBuilder(new String[]{"test"},new String[]{"test"}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}