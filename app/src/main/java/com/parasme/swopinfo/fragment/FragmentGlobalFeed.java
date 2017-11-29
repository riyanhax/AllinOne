package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.LocationActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FeedAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.RippleBackground;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.model.Retailer;
import com.parasme.swopinfo.model.Store;
import com.parasme.swopinfo.model.Upload;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.parasme.swopinfo.helper.Utils.createThumbURL;


/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentGlobalFeed extends BaseFragment {

    private View childView;
    public static ListView listFeeds;
    private ArrayList<Feed> globalFeed = new ArrayList<>();
    private LinearLayout layoutOwnProfile;
    private ImageView imageUser;
    private LinearLayout layout;
    public static Dialog dialogSwop;
    public ArrayList<File> fileArrayList=new ArrayList<>();
    private String TAG = getClass().getName();
    public static TextView textFilesCount;
    public static FeedAdapter adapter;
    private ImageView imgChecking;
    public static ArrayList<Retailer> retailerList;


    TextView textSelection;
    private final int PICK_PHOTO=22, PICK_VIDEO=33, PICK_DOC=44, PICK_VOICE=55;
    private RelativeLayout layoutSelection;
    private LinearLayout layoutPick;

    public static RippleBackground rippleBackground;

    @Override
    public void onResume() {
        super.onResume();

        retailerList = new ArrayList<>();

        imageActionFeed.setImageResource(R.drawable.ic_newsfeed_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Home");

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);

        listFeeds = (ListView) childView.findViewById(R.id.listFeeds);

        ViewGroup header = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.home_header_layout,listFeeds,false);
        listFeeds.addHeaderView(header);

        layoutOwnProfile = (LinearLayout) childView.findViewById(R.id.layoutOwnProfile);
        imageUser = (ImageView) listFeeds.findViewById(R.id.imageUser);

        Picasso.with(mActivity).load(AppConstants.USER_IMAGE_URL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageUser);


        layout = (LinearLayout) baseView.findViewById(R.id.layout);
        layout.removeAllViews();
        layout.addView(childView);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.replaceFragment(new FragmentProfile(), getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        ((LinearLayout) childView.findViewById(R.id.layoutShareSomething)).setVisibility(View.VISIBLE);
        ((EditText) childView.findViewById(R.id.editSwopText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialogSwop==null)
                loadDialog();
//                dialogSwop.show();
            }
        });


        getGlobalFeeds();


        imgChecking = (ImageView) childView.findViewById(R.id.img_checkin);


        rippleBackground = (RippleBackground)childView.findViewById(R.id.rippleBackground);
        rippleBackground.startRippleAnimation();


        // If user is of south africa then only show check in button
        //if (MainActivity.fullAddress.contains("South Africa")) {
        if (1==1) {
            Log.e("VISI","VISIBLE");
            rippleBackground.setVisibility(View.VISIBLE);
        }

        imgChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LOCATION",LocationActivity.mCurrentLocation.getLatitude()+"");
                if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_CHECK_IN_INTRO,false))
                    checkIn(LocationActivity.mCurrentLocation.getLatitude()+"",LocationActivity.mCurrentLocation.getLongitude()+"", AppConstants.USER_ID);
                else
                    showIntroDialog();

/*
                if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_CHECK_IN_INTRO,false))
                    ((MainActivity)appCompatActivity).replaceFragment(new FragmentRetailerLogos(),getFragmentManager(),appCompatActivity,R.id.content_frame);
                else
                    showIntroDialog();
*/
            }
        });
    }

    private void getGlobalFeeds() {
        for (int i = 0; i < FragmentHome.feedArrayList.size(); i++) {
            if (FragmentHome.feedArrayList.get(i).getType().equals("user")
                    || FragmentHome.feedArrayList.get(i).getCompanyId()==54)
                globalFeed.add(FragmentHome.feedArrayList.get(i));

        }
        listFeeds.setAdapter(new FeedAdapter(mActivity, R.layout.row_feed, globalFeed, listFeeds));
    }


    private void checkIn(String latitude, String longitude, String userId) {
        String catIds = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_FAV_IDS);
        Log.e("catids", catIds);
        String url = "https://swopinfo.com/retailerswithpromo.aspx?cat_id="+catIds+"&retailer_lat="+latitude+
                "&retailer_long="+longitude;

        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Checkin",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.opt("Result") instanceof JSONArray){
                                JSONArray jsonArray = jsonObject.optJSONArray("Result");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject retailerObject = jsonArray.getJSONObject(i);
                                    Retailer retailer = new Retailer();
                                    retailer.setRetailerLogo(retailerObject.optString("storelogo"));
                                    retailer.setStoreId(retailerObject.optString("storeID"));

                                    ArrayList<Store.Promotion> promotionList = new ArrayList<>();

                                    JSONArray promotionArray = retailerObject.optJSONArray("pro");
                                    for (int j = 0; j < promotionArray.length(); j++) {
                                        JSONObject promotionObject = promotionArray.optJSONObject(j);
                                        Store.Promotion promotion = new Store.Promotion();
                                        promotion.setImageURL(promotionObject.optString("promotionimg"));
                                        promotionList.add(promotion);
                                    }

                                    retailer.setPromotions(promotionList);
                                    if(promotionArray.length()!=0)
                                        retailerList.add(retailer);
                                }

                                Retailer retailer = new Retailer();
                                retailer.setStoreId("0");
                                retailer.setRetailerLogo("https://www.ecigssa.co.za/data/attachments/99/99598-ff08bbcb3dbbe9846619354ee13b48d2.jpg");
                                retailerList.add(retailer);

                                ((MainActivity)mActivity).replaceFragment(new FragmentRetailerLogos(),getFragmentManager(),mActivity,R.id.content_frame);

                            }
                            else
                                alertDialog(mActivity,"Thanks for Checking in. We unfortunately do not have stores listed in this Geolocated area but will have soon. Please ask your Local Retailers to join so that you can benefit.", "Check In");
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showIntroDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_promotions);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        TextView textChooseFav = (TextView) dialog.findViewById(R.id.text_choose_favs);
        textChooseFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.replaceFragment(new FragmentSelectCategories(),getFragmentManager(),mActivity,R.id.content_frame);
            }
        });

        dialog.show();
    }




    @Override
    public void onDetach() {
        super.onDetach();
        layout.removeAllViews();
    }

    private void loadDialog() {
        final Dialog dialogSwop = new Dialog(mActivity);
        dialogSwop.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSwop.setContentView(R.layout.dialog_swop);
        dialogSwop.setCancelable(true);
        dialogSwop.setCanceledOnTouchOutside(true);


        final EditText editSwop = (EditText) dialogSwop.findViewById(R.id.editSwop);
        final Button btnPost = (Button) dialogSwop.findViewById(R.id.btnPost);
        textSelection = (TextView) dialogSwop.findViewById(R.id.text_selection);
        layoutSelection = (RelativeLayout) dialogSwop.findViewById(R.id.layout_selection);
        layoutPick = (LinearLayout) dialogSwop.findViewById(R.id.layout_pick);
        final LinearLayout layoutPhoto = (LinearLayout) dialogSwop.findViewById(R.id.layout_photo);
        final LinearLayout layoutVideo = (LinearLayout) dialogSwop.findViewById(R.id.layout_video);
        final LinearLayout layoutDoc = (LinearLayout) dialogSwop.findViewById(R.id.layout_doc);
        final LinearLayout layoutVoice = (LinearLayout) dialogSwop.findViewById(R.id.layout_voice_note);
        final ImageView imgDeselect = (ImageView) dialogSwop.findViewById(R.id.img_deselect);

        layoutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Photo"),PICK_PHOTO);

            }
        });

        layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO);

            }
        });

        layoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio"),PICK_VOICE);

            }
        });

        layoutDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent .setType("*/*");
                String[] mimetypes = {"application/pdf",
                        "text/*",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, PICK_DOC);


//                Intent intent = new Intent();
//                intent.setType("audio/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Audio"),PICK_VOICE);

            }
        });


        imgDeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPick.setVisibility(View.VISIBLE);
                layoutSelection.setVisibility(View.GONE);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String swopText = editSwop.getText().toString();
                if (swopText.equals("") && fileArrayList.size()==0){
                    editSwop.setError("Please enter some text share or select a file");
                    editSwop.requestFocus();
                    return;
                }
                dialogSwop.dismiss();
                new FragmentAdd().uploadFile(AppConstants.USER_ID, "0", "0", "", " ", " ", " ", "true", "true", "true",
                        "true", "true", "", fileArrayList, mActivity, null, swopText, false);
//                new FragmentAdd().uploadClick(editSwop, editFolderName, editTitle, editDescription, editYoutubeLink, editTag, spinnerBroadcast, checkBoxComments, checkBoxRatings, checkBoxEmbedded, checkBoxDownloads, appCompatActivity, AppConstants.USER_ID, "0", "0", fileArrayList);

            }
        });

        dialogSwop.show();
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
        WebServiceHandler.call.cancel();
    }


    public void alertDialog(final Activity activity, final String message, final String title) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(message);
        adb.setTitle(title);
        adb.setCancelable(false);
        adb.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("Select Categories",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.replaceFragment(new FragmentSelectCategories(),getFragmentManager(),mActivity,R.id.content_frame);
            }
        });

        adb.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("aaaaa","aaaaa");
        String path="";
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode){
                case PICK_PHOTO:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Photo Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_VIDEO:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Video Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_DOC:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Document Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_VOICE:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Voice Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
            }

        }
    }




}