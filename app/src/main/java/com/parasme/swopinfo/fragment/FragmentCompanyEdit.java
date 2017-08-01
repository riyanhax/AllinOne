package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;

/**
 * Created by :- Mukesh Kumawat on 02-Feb-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentCompanyEdit extends Fragment implements ImagePicker.Picker {

    protected AppCompatActivity mActivity;
    protected View baseView;
    private MaterialEditText editCompanyHandle,editCompanyName,editCompanyIndustry,editCompanyInfo1,editCompanyInfo2,editCompanyAddress1,
            editCompanyAddress2,editCompanyCity,editCompanyCountry,editCompanyCode,editCompanyEmail,editCompanyTel1,editCompanyTel2,editCompanyFax,
            editCompanyCell,editCompanyFacebookURL,editCompanyWebURL,editCompanyTwitterURL;

    private Button btnUpdate;
    private ImageView imageCompany;
    private ScrollView scrollView;
    MaterialEditText view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_business_about_edit, container, false);

        scrollView = (ScrollView) baseView.findViewById(R.id.scrollView);
        editCompanyHandle = (MaterialEditText) baseView.findViewById(R.id.editCompanyHandle);
        editCompanyName = (MaterialEditText) baseView.findViewById(R.id.editCompany);
        editCompanyIndustry = (MaterialEditText) baseView.findViewById(R.id.editCompanyIndustry);
        editCompanyInfo1 = (MaterialEditText) baseView.findViewById(R.id.editCompanyInfo1);
        editCompanyInfo2 = (MaterialEditText) baseView.findViewById(R.id.editCompanyInfo2);
        editCompanyAddress1 = (MaterialEditText) baseView.findViewById(R.id.editCompanyAddress1);
        editCompanyAddress2 = (MaterialEditText) baseView.findViewById(R.id.editCompanyAddress2);
        editCompanyCity = (MaterialEditText) baseView.findViewById(R.id.editCompanyCity);
        editCompanyCountry = (MaterialEditText) baseView.findViewById(R.id.editCompanyCountry);
        editCompanyCode = (MaterialEditText) baseView.findViewById(R.id.editCompanyPostCode);
        editCompanyEmail = (MaterialEditText) baseView.findViewById(R.id.editCompanyEmail);
        editCompanyTel1 = (MaterialEditText) baseView.findViewById(R.id.editCompanyTel1);
        editCompanyTel2 = (MaterialEditText) baseView.findViewById(R.id.editCompanyTel2);
        editCompanyFax = (MaterialEditText) baseView.findViewById(R.id.editCompanyFax);
        editCompanyCell = (MaterialEditText) baseView.findViewById(R.id.editCompanyCell);
        editCompanyFacebookURL = (MaterialEditText) baseView.findViewById(R.id.editCompanyFacebook);
        editCompanyWebURL = (MaterialEditText) baseView.findViewById(R.id.editCompanyWebsite);
        editCompanyTwitterURL = (MaterialEditText) baseView.findViewById(R.id.editCompanyTwitter);

        btnUpdate = (Button) baseView.findViewById(R.id.btnUpdateProfle);

        setData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndUpdate();
            }
        });

        imageCompany = (ImageView) baseView.findViewById(R.id.imageCompany);

        String imageURL=AppConstants.URL_DOMAIN+"upload/company"+ FragmentCompany.companyId+"/logo.jpg";
        Picasso.with(mActivity).load(imageURL)
                .error(R.drawable.companylogo)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(imageCompany);

        imageCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.picker = FragmentCompanyEdit.this;
                startActivity(new Intent(mActivity,ImagePicker.class));
            }
        });
        return baseView;
    }

    private void setData() {
        JSONObject jsonObject = FragmentCompany.jsonObject;
        editCompanyName.setText(jsonObject.optString("CompanyName"));
        editCompanyIndustry.setText(jsonObject.optString("CompanyIndustry"));
        editCompanyInfo1.setText(jsonObject.optString("ExtraInfo1"));
        editCompanyInfo2.setText(jsonObject.optString("ExtraInfo2"));
        editCompanyAddress1.setText(jsonObject.optString("BussinessAdd1"));
        editCompanyAddress2.setText(jsonObject.optString("BussinessAdd2"));
        editCompanyCity.setText(jsonObject.optString("BussinessCity"));
        editCompanyCountry.setText(jsonObject.optString("BussinessCountry"));
        editCompanyCode.setText(jsonObject.optString("BussinessCode"));
        editCompanyEmail.setText(jsonObject.optString("EmailAddress"));
        editCompanyTel1.setText(jsonObject.optString("BussinessTel1"));
        editCompanyTel2.setText(jsonObject.optString("BussinessTel2"));
        editCompanyFax.setText(jsonObject.optString("BussinessFax"));
        editCompanyCell.setText(jsonObject.optString("BussinessCell"));
        editCompanyFacebookURL.setText(jsonObject.optString("facebooklink"));
        editCompanyTwitterURL.setText(jsonObject.optString("twitterlink"));
        editCompanyWebURL.setText(jsonObject.optString("websitelink"));
        editCompanyHandle.setText(jsonObject.optString("CompanyHandle"));
    }

    private void validateAndUpdate() {
        view = null;
        String strCompanyHandle,strCompanyName,strCompanyIndustry,strCompanyInfo1,strCompanyInfo2,strCompanyAddress1,
                strCompanyAddress2,strCompanyCity,strCompanyCountry,strCompanyCode,strCompanyTel1,strCompanyTel2,strCompanyFax,
                strCompanyCell,strCompanyFacebookURL,strCompanyWebURL,strCompanyTwitterURL,strCompanyEmail;

        strCompanyHandle = editCompanyHandle.getText().toString();
        strCompanyName = editCompanyName.getText().toString();
        strCompanyIndustry = editCompanyIndustry.getText().toString();
        strCompanyInfo1 = editCompanyInfo1.getText().toString();
        strCompanyInfo2 = editCompanyInfo2.getText().toString();
        strCompanyAddress1 = editCompanyAddress1.getText().toString();
        strCompanyAddress2 = editCompanyAddress2.getText().toString();
        strCompanyCity = editCompanyCity.getText().toString();
        strCompanyCountry = editCompanyCountry.getText().toString();
        strCompanyCode = editCompanyCode.getText().toString();
        strCompanyEmail = editCompanyEmail.getText().toString();
        strCompanyTel1 = editCompanyTel1.getText().toString();
        strCompanyTel2 = editCompanyTel2.getText().toString();
        strCompanyFax = editCompanyFax.getText().toString();
        strCompanyCell = editCompanyCell.getText().toString();
        strCompanyFacebookURL = editCompanyFacebookURL.getText().toString();
        strCompanyWebURL = editCompanyWebURL.getText().toString();
        strCompanyTwitterURL = editCompanyTwitterURL.getText().toString();
        
        if(strCompanyHandle.equals(""))
            view = editCompanyHandle;
        else if(strCompanyName.equals(""))
            view = editCompanyName;
        else if(strCompanyInfo1.equals(""))
            view = editCompanyInfo1;
        else if(strCompanyAddress1.equals(""))
            view = editCompanyAddress1;
        else if(strCompanyCity.equals(""))
            view = editCompanyCity;
        else if(strCompanyCountry.equals(""))
            view = editCompanyCountry;

        if(view!=null){
            view.setError("Field can not be empty");
            view.requestFocus();
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, view.getTop()-5);
                }
            });
        }
        else
            updateCompanyProfile(AppConstants.URL_COMAPNY + "update", FragmentCompany.companyId+"", strCompanyHandle,strCompanyName,strCompanyIndustry,strCompanyInfo1,strCompanyInfo2,strCompanyAddress1,
                    strCompanyAddress2,strCompanyCity,strCompanyCountry,strCompanyCode,strCompanyEmail, strCompanyTel1,strCompanyTel2,strCompanyFax,
                    strCompanyCell,strCompanyFacebookURL,strCompanyWebURL,strCompanyTwitterURL);
    }

    private void updateCompanyProfile(String url, String companyId, String strCompanyHandle, String strCompanyName, String strCompanyIndustry, String strCompanyInfo1, String strCompanyInfo2, String strCompanyAddress1, String strCompanyAddress2, String strCompanyCity, String strCompanyCountry, String strCompanyCode, String strCompanyEmail, String strCompanyTel1, String strCompanyTel2, String strCompanyFax, String strCompanyCell, String strCompanyFacebookURL, String strCompanyWebURL, String strCompanyTwitterURL) {
        String [] paramsArray = {"companyid","CompanyName","CompanyIndustry","ExtraInfo1","ExtraInfo2","BussinessAdd1","BussinessAdd2",
                "BussinessCell", "BussinessCity","BussinessCode","BussinessCountry","EmailAddress","BussinessTel1","BussinessTel2","BussinessFax",
                "ownerid","facebooklink", "twitterlink","websitelink","companylogo","companyhandle","Active"};

        String [] valuesArray = {companyId, strCompanyName, strCompanyIndustry, strCompanyInfo1, strCompanyInfo2, strCompanyAddress1,
                strCompanyAddress2,strCompanyCell,strCompanyCity,strCompanyCode,strCompanyCountry, strCompanyEmail, strCompanyTel1,strCompanyTel2,strCompanyFax,
                AppConstants.USER_ID,strCompanyFacebookURL,strCompanyTwitterURL,strCompanyWebURL,"true",strCompanyHandle,"true"};

        FormBody.Builder builder = WebServiceHandler.createBuilder(paramsArray,valuesArray);

        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("UPDATE", "onResponse: "+response );
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("status").equals("200"))
                                MyApplication.alertDialog(mActivity,"Company Profile Updated Successfully","Company Profile Update");
                            else
                                MyApplication.alertDialog(mActivity,"Company Profile Could Not Be Updated","Company Profile Update");

                        }   catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(url,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void onImagePicked(final Bitmap bitmap, final String imagePath) {
        Log.e("FINALIMAGEPATH",imagePath);
        AlertDialog.Builder adb = new AlertDialog.Builder(mActivity);
        adb.setMessage("Update company profile picture with selected image ?");
        adb.setTitle("Update Company Profile");
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
                                MyApplication.alertDialog(mActivity,"Company profile picture updated successfully","Update Picture");
                                imageCompany.setImageBitmap(bitmap);
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not update profile picture","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_COMAPNY + FragmentCompany.companyId + "/profilepic" , WebServiceHandler.createMultiPartBuilder(new String[]{"profilepic"},new String[]{imagePath}),AppConstants.USER_ID);
        }catch (IOException e){}
    }

}
