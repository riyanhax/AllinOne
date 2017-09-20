package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.SignUpActivity;
import com.parasme.swopinfo.activity.SubscriptionPaymentActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
/*
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.FormBody;

/**
 * Created by SoNu on 4/3/2017.
 */

public class FragmentSubscriptionPayment extends Fragment implements View.OnClickListener {

    private Button btnReadMore, btnClickHere, btnSubmit;
    private EditText editEmail, editFName, editLName, editUserName, editDOB, editCountry, editPassword, editConfirmPassword, editCompanyName,
            editHandle, editIndustry, editAddress1, editAddress2, editCity, editPostCode, editExtraInfo1, editExtraInfo2,
            editCompanyEmail, editTel1, editTel2, editFax, editCell, editFacebook, editTwitter, editWebsite;
    private RadioGroup radioGroupPay;
    private LinearLayout layoutCompanyMoreFields;
    private ArrayList<String> countryNameList = new ArrayList<>();
    private Dialog dialogCountry;
    private ListView listViewCountry;
    private String referenceGUID;

    /**
     *
     * THIS FILE IS OVERWRITTEN BY `androidSDK/src/<general|partner>sampleAppJava.
     * ANY UPDATES TO THIS FILE WILL BE REMOVED IN RELEASES.
     *
     * Basic sample using the SDK to make a payment or consent to future payments.
     *
     * For sample mobile backend interactions, see
     * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
     */
    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AepaOoPFDHrA2kWtQYjX4yynFAgN74eublUJUwwKBVQrlRuBBefn0jKieV9usbBX3bf3f23L3Z67wbGE";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("SwopInfo")
            .acceptCreditCards(true)
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscription_payment, container, false);
        initializeViews(rootView);

        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        setClickListeners();

        loadCountryDialog();
        return rootView;
    }

    private void setClickListeners() {

        editDOB.setOnClickListener(this);
        editCountry.setOnClickListener(this);
        btnReadMore.setOnClickListener(this);
        btnClickHere.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initializeViews(View rootView) {
        btnReadMore = (Button) rootView.findViewById(R.id.btnReadMore);
        btnClickHere = (Button) rootView.findViewById(R.id.btnClickHere);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        editFName = (EditText) rootView.findViewById(R.id.editFirstName);
        editLName = (EditText) rootView.findViewById(R.id.editLastName);
        editUserName = (EditText) rootView.findViewById(R.id.editUserName);
        editDOB = (EditText) rootView.findViewById(R.id.editDOB);
        editCountry = (EditText) rootView.findViewById(R.id.editCountry);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editCompanyName = (EditText) rootView.findViewById(R.id.editCompanyName);
        editHandle = (EditText) rootView.findViewById(R.id.editHanlde);
        editIndustry = (EditText) rootView.findViewById(R.id.editIndustry);
        editAddress1 = (EditText) rootView.findViewById(R.id.editAddress1);
        editAddress2 = (EditText) rootView.findViewById(R.id.editAddress2);
        editCity = (EditText) rootView.findViewById(R.id.editCity);
        editPostCode = (EditText) rootView.findViewById(R.id.editPostCode);
        editExtraInfo1 = (EditText) rootView.findViewById(R.id.editExtraInfo1);
        editExtraInfo2 = (EditText) rootView.findViewById(R.id.editExtraInfo2);
        editCompanyEmail = (EditText) rootView.findViewById(R.id.editCompanyEmail);
        editTel1 = (EditText) rootView.findViewById(R.id.editTel1);
        editTel2 = (EditText) rootView.findViewById(R.id.editTel2);
        editFax = (EditText) rootView.findViewById(R.id.editFax);
        editCell = (EditText) rootView.findViewById(R.id.editCell);
        editFacebook = (EditText) rootView.findViewById(R.id.editFacebook);
        editTwitter = (EditText) rootView.findViewById(R.id.editTwitter);
        editWebsite = (EditText) rootView.findViewById(R.id.editWebsite);
        radioGroupPay = (RadioGroup) rootView.findViewById(R.id.radioGroupPay);

        layoutCompanyMoreFields = (LinearLayout) rootView.findViewById(R.id.layoutMoreCompanyFields);
    }

    private void validate() {
        ArrayList<EditText> editTextArrayList = new ArrayList<>();
        editTextArrayList.add(editEmail);
        editTextArrayList.add(editFName);
        editTextArrayList.add(editLName);
        editTextArrayList.add(editUserName);
        editTextArrayList.add(editDOB);
        editTextArrayList.add(editPassword);
        editTextArrayList.add(editConfirmPassword);
        editTextArrayList.add(editCompanyName);
        editTextArrayList.add(editHandle);

        String email = editEmail.getText().toString();
        String fname = editFName.getText().toString();
        String lname = editLName.getText().toString();
        String username = editUserName.getText().toString();
        String dob = editDOB.getText().toString();
        String country = editCountry.getText().toString();
        String password = editPassword.getText().toString();
        String confPassword = editConfirmPassword.getText().toString();
        String companyName = editCompanyName.getText().toString();
        String handle = editHandle.getText().toString();
        String isYearly = radioGroupPay.getCheckedRadioButtonId() == R.id.radio_year ? "true" : "false";

        String industry = editIndustry.getText().toString();
        String address1 = editAddress1.getText().toString();
        String address2 = editAddress2.getText().toString();
        String city = editCity.getText().toString();
        String postCode = editPostCode.getText().toString();
        String extrainfo = editExtraInfo1.getText().toString();
        String extrainfo2 = editExtraInfo2.getText().toString();
        String companyEmail = editCompanyEmail.getText().toString();
        String tel1 = editTel1.getText().toString();
        String tel2 = editTel2.getText().toString();
        String fax = editFax.getText().toString();
        String cell = editCell.getText().toString();
        String twitter = editTwitter.getText().toString();
        String facebook = editFacebook.getText().toString();
        String website = editWebsite.getText().toString();

        for (int i = 0; i < editTextArrayList.size(); i++) {
            EditText editText = editTextArrayList.get(i);
            if(editText.getText().toString().equals("")){
                editText.setError("Please enter "+editText.getHint()+"");
                editText.requestFocus();
                return;
            }
        }

        if(!password.equals(confPassword)){
            editConfirmPassword.setError("Password didn't match");
            editConfirmPassword.requestFocus();
            return;
        }

        Log.e("Ok","ok");
        register(email, fname, lname, username,dob,country,password,companyName,handle,isYearly,industry,address1,address2,
                city,postCode,extrainfo,extrainfo2,companyEmail,tel1,tel2,fax,cell,twitter,facebook,website);
    }

    private void register(String email, String fname, String lname, String username, String dob, String country, String password,
                          String companyName, String handle, final String isYearly, String industry,
                          String address1, String address2, String city, String postCode, String extrainfo,
                          String extrainfo2, String companyEmail, String tel1, String tel2, String fax, String cell,
                          String twitter, String facebook, String website) {

        String [] paramArray = {"Pro","userEmail","userFirstname","userLastname","username","bday","BussinessCountry","Password","CompanyName",
                "CompanyHandle","Yearly","CompanyIndustry","BussinessAdd1","BussinessAdd2","BussinessCity",
                "BussinessCode","ExtraInfo1","ExtraInfo2","CompanyEmailAddress","BussinessTel1","BussinessTel2",
                "BussinessFax","BussinessCell","twitterlink","facebooklink","websitelink"};
        String [] valueArray = {"true",email,fname,lname,username,dob,country,password,companyName,handle,isYearly,industry,address1,address2,
                city,postCode,extrainfo,extrainfo2,companyEmail,tel1,tel2,fax,cell,twitter,facebook,website};

        FormBody.Builder builder = WebServiceHandler.createBuilder(paramArray,valueArray);
        WebServiceHandler webServiceHandler = new WebServiceHandler(getActivity());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Business Resp",response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.opt("returnvalue") instanceof JSONObject){
                                JSONObject jsonObject1 = jsonObject.optJSONObject("returnvalue");
                                referenceGUID = jsonObject1.optString("ReferenceGUID");
                                String chargeAmount = isYearly.equalsIgnoreCase("true") ? "399" : "39";
                                chargePayPal(chargeAmount);
                            }
                            else
                                MyApplication.alertDialog(getActivity(),"Could not process the request","Register Unsuccessful");
                        }
                        catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_COMAPNY_REGISTER,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargePayPal(String chargeAmount) {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, chargeAmount);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    private PayPalPayment getThingToBuy(String paymentIntent, String amount) {
        return new PayPalPayment(new BigDecimal(amount), "USD", "Business Account",
                paymentIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG+" 1", confirm.toJSONObject().toString(4));
                        Log.e(TAG+" 2", confirm.getPayment().toJSONObject().toString(4));

                        activateCompany();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.e("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.e("FuturePaymentExample", authorization_code);


                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.e("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.e("ProfileSharingExample", authorization_code);


                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editDOB:
                new Utils(getActivity()).selectDate(editDOB);
                break;
            case R.id.btnReadMore:
                MyApplication.alertDialog(getActivity(),getResources().getString(R.string.subscription_description),"Business Account");
                break;
            case R.id.btnClickHere:
                //layoutCompanyMoreFields.setVisibility(layoutCompanyMoreFields.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                layoutCompanyMoreFields.setVisibility(View.VISIBLE);
                break;
            case R.id.editCountry:
                dialogCountry.show();
                break;
            case R.id.btnSubmit:
                validate();
                break;
        }
    }

    private void validateAndRegister() {
        ArrayList<EditText> editTextArrayList = new ArrayList<>();
        editTextArrayList.add(editEmail);
        editTextArrayList.add(editFName);
        editTextArrayList.add(editLName);
        editTextArrayList.add(editUserName);
        editTextArrayList.add(editDOB);
        editTextArrayList.add(editCountry);
        editTextArrayList.add(editPassword);
        editTextArrayList.add(editConfirmPassword);
        editTextArrayList.add(editCompanyName);
        editTextArrayList.add(editHandle);

        String email = editEmail.getText().toString();
        String fname = editFName.getText().toString();
        String lname = editLName.getText().toString();
        String username = editUserName.getText().toString();
        String dob = editDOB.getText().toString();
        String country = editCountry.getText().toString();
        String password = editPassword.getText().toString();
        String confPassword = editConfirmPassword.getText().toString();
        String companyName = editCompanyName.getText().toString();
        String handle = editHandle.getText().toString();
        String isYearly = radioGroupPay.getCheckedRadioButtonId() == R.id.radio_year ? "true" : "false";

        String industry = editIndustry.getText().toString();
        String address1 = editAddress1.getText().toString();
        String address2 = editAddress2.getText().toString();
        String city = editCity.getText().toString();
        String postCode = editPostCode.getText().toString();
        String extrainfo = editExtraInfo1.getText().toString();
        String extrainfo2 = editExtraInfo2.getText().toString();
        String companyEmail = editCompanyEmail.getText().toString();
        String tel1 = editTel1.getText().toString();
        String tel2 = editTel2.getText().toString();
        String fax = editFax.getText().toString();
        String cell = editCell.getText().toString();
        String twitter = editTwitter.getText().toString();
        String facebook = editFacebook.getText().toString();
        String website = editWebsite.getText().toString();

        boolean isFieldMissing = false;
        for (int i = 0; i < editTextArrayList.size(); i++) {
            EditText editText = editTextArrayList.get(i);
            if(editText.getText().toString().equals("")){
                editText.setError("Please enter "+editText.getHint()+"");
                editText.requestFocus();
                isFieldMissing = true;
                return;
            }
        }

        Log.e("Ok","ok");

    }

    public void loadCountryDialog() {

        countryNameList= new Utils(getActivity()).getCountryNames();

        Object[] objects=new Utils(getActivity()).loadCountryDialog(countryNameList);
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

    private void activateCompany() {
        FormBody.Builder builder = WebServiceHandler.createBuilder(new String[]{"ReferenceGUID"}, new String[]{referenceGUID});
        WebServiceHandler webServiceHandler = new WebServiceHandler(getActivity());
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Activate Com",response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optString("status").equals("200"))
                                MyApplication.alertDialog(getActivity(),"Verify mail is sent to your email id","Alert");
                            else
                                MyApplication.alertDialog(getActivity(),"Could not activate the company profile","Company Activation");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_COMAPNY_ACTIVATE,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}