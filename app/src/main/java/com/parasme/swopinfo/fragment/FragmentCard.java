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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.Shimmer;
import com.parasme.swopinfo.helper.ShimmerTextView;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.parasme.swopinfo.helper.Utils.openDialerActivity;
import static com.parasme.swopinfo.helper.Utils.openEmailClients;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentCard extends Fragment implements ImagePicker.Picker, View.OnClickListener {
    private TextView textUserName,textCompanyName;
    private ShimmerTextView textCell,textEmail,textTel1,textTel2,textFax;
    private AppCompatActivity mActivity;
    private Button btnCardAction;
    private ImageView imageCard;
    private LinearLayout layoutDefaultCard;
    private boolean isCardAvailable=false;
    private int cardId;
    private String cardURL,userId;
    private String cardURLtoShare;
    private JSONObject returnObject;
    private CircleImageView imgShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        textUserName = (TextView) view.findViewById(R.id.textUserName);
        textCompanyName = (TextView) view.findViewById(R.id.textCompanyName);
        textEmail = (ShimmerTextView) view.findViewById(R.id.textEmail);
        textCell = (ShimmerTextView) view.findViewById(R.id.textCell);
        textTel1 = (ShimmerTextView) view.findViewById(R.id.textTel1);
        textTel2 = (ShimmerTextView) view.findViewById(R.id.textTel2);
        textFax = (ShimmerTextView) view.findViewById(R.id.textFax);
        btnCardAction = (Button) view.findViewById(R.id.btnCardAction);
        imageCard = (ImageView) view.findViewById(R.id.imageCard);
        layoutDefaultCard = (LinearLayout) view.findViewById(R.id.layoutDefaultCard);
        imgShare = (CircleImageView) view.findViewById(R.id.imgShare);

        Shimmer shimmer1 = new Shimmer();
        Shimmer shimmer2 = new Shimmer();
        Shimmer shimmer4 = new Shimmer();
        shimmer1.start(textEmail);
        shimmer2.setDirection(1);
        shimmer2.start(textCell);
        shimmer1.start(textTel1);
        shimmer4.setDirection(1);
        shimmer4.start(textTel2);
        shimmer1.start(textFax);

        try {
            // If user is visiting other's card then action button should be hidden
            userId = this.getArguments().getString(AppConstants.KEY_USER_ID);
            cardURLtoShare = AppConstants.URL_DOMAIN+"bcard.cshtml?carduser="+userId;

            if(!userId.equals(AppConstants.USER_ID))
                btnCardAction.setVisibility(View.GONE);

            getBussCard(AppConstants.URL_CARD + userId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnCardAction.setOnClickListener(this);
        textCell.setOnClickListener(this);
        textEmail.setOnClickListener(this);
        textTel1.setOnClickListener(this);
        textTel2.setOnClickListener(this);
        textFax.setOnClickListener(this);
        imgShare.setOnClickListener(this);

        return view;
    }

    private void removeCard() {
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Remove Resp",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                MyApplication.alertDialog(mActivity,"Card removed successfully","Business Card");
                                imageCard.setVisibility(View.GONE);
                                layoutDefaultCard.setVisibility(View.VISIBLE);
                                btnCardAction.setText("Upload your own");
                                loadDefaultCard(returnObject);
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not remove business card","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.post(AppConstants.URL_CARD+"remove" , WebServiceHandler.createBuilder(new String[]{"userid"},new String[]{AppConstants.USER_ID}));
        }catch (IOException e){}

    }

    private void getBussCard(String url) throws IOException {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("Card", "run: "+response );
                            returnObject = new JSONObject(response).optJSONObject("returnvalue");
                            JSONObject cardObject = returnObject.optJSONObject("businesscard");

                        if (cardObject==null){
                            isCardAvailable = false;
                            imageCard.setVisibility(View.GONE);
                            layoutDefaultCard.setVisibility(View.VISIBLE);
                            btnCardAction.setText("Upload your own");
                            loadDefaultCard(returnObject);
                        }

                        else {
                                cardId = cardObject.optInt("cardid");
                                cardURL = AppConstants.URL_DOMAIN+cardObject.optString("coordsurl");
                                imageCard.setVisibility(View.VISIBLE);
                                layoutDefaultCard.setVisibility(View.INVISIBLE);
                                btnCardAction.setText("Remove Card");
                                Picasso.with(mActivity).load(cardURL).placeholder(R.drawable.app_icon).error(android.R.drawable.stat_notify_error).into(imageCard);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        webServiceHandler.get(url);
    }

    private void loadDefaultCard(JSONObject jsonObject) {

        String email = jsonObject.optString("businessEmail");
        String cell = jsonObject.optString("businessCell");
        String tel = jsonObject.optString("businessDirecttel");
        String fax = jsonObject.optString("businesscustomfax");
        String companyName = jsonObject.optString("businessCompanyName");

        textUserName.setText(EmojiHandler.decodeJava(jsonObject.optString("userFirstname")) + " " +
                EmojiHandler.decodeJava(jsonObject.optString("userLastname")));
        textEmail.setText((!email.equals("null") && !email.equals("")) ? email : "Not Available");
        textCell.setText((!cell.equals("null") && !cell.equals("")) ? cell : "Not Available");
        textTel1.setText((!tel.equals("null") && !tel.equals("")) ? tel : "Not Available");
        textTel2.setText((!tel.equals("null") && !tel.equals("")) ? tel : "Not Available");
        textFax.setText((!fax.equals("null") && !fax.equals("")) ? fax : "Not Available");

        if(companyName.equals("null") || companyName.equals("")){
            textCompanyName.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)textUserName.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            textUserName.setLayoutParams(layoutParams);
        }
        else
            textCompanyName.setText(jsonObject.optString("businessCompanyName"));

    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Business Card");
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
        adb.setMessage("Upload card with selected image ?");
        adb.setTitle("Business Card");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadCard(bitmap,imagePath);
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    @Override
    public void onVideoPicked(String videoPath) {

    }

    private void uploadCard(final Bitmap bitmap, String imagePath) {
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Card Resp",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                MyApplication.alertDialog(mActivity,"Card uploaded successfully","Business Card");
                                imageCard.setVisibility(View.VISIBLE);
                                layoutDefaultCard.setVisibility(View.INVISIBLE);
                                imageCard.setImageBitmap(bitmap);
                                btnCardAction.setText("Remove Card");
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not upload business card","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_CARD+"upload" , WebServiceHandler.createMultiPartBuilder(new String[]{"userid","file"},new String[]{AppConstants.USER_ID,imagePath}),AppConstants.USER_ID);
        }catch (IOException e){}

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCardAction:
                if(btnCardAction.getText().toString().contains("Remove")){
                    AlertDialog.Builder adb = new AlertDialog.Builder(mActivity);
                    adb.setMessage("Remove uploaded card?");
                    adb.setTitle("Business Card");
                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeCard();
                        }
                    });
                    adb.setNegativeButton("No", null);
                    adb.show();

                }
                else
                {
                    ImagePicker.picker=FragmentCard.this;
                    startActivity(new Intent(mActivity,ImagePicker.class));
                }
                break;

            case R.id.textEmail:
                openEmailClients(mActivity, textEmail.getText().toString());
                break;
            case R.id.textCell:
                openDialerActivity(mActivity,textCell.getText().toString());
                break;
            case R.id.textTel1:
                openDialerActivity(mActivity,textTel1.getText().toString());
                break;
            case R.id.textTel2:
                openDialerActivity(mActivity,textTel2.getText().toString());
                break;
            case R.id.textFax:
                openDialerActivity(mActivity,textFax.getText().toString());
                break;
            case R.id.imgShare:
                Utils.shareURLCustomIntent(cardURLtoShare, mActivity);
                break;
        }
    }
}