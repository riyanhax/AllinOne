package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hendraanggrian.widget.SocialAutoCompleteTextView;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FolderAdapter;
import com.parasme.swopinfo.adapter.FollowerAdapter;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Shimmer;
import com.parasme.swopinfo.helper.ShimmerTextView;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Folder;
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
import java.util.ArrayList;

import okhttp3.FormBody;

import static com.parasme.swopinfo.fragment.FragmentAdd.broadcastArray;
import static com.parasme.swopinfo.fragment.FragmentFolders.folderArrayList;
import static com.parasme.swopinfo.fragment.FragmentUploads.adapter;
import static com.parasme.swopinfo.fragment.FragmentUploads.uploadArrayList;
import static com.parasme.swopinfo.helper.Utils.openDialerActivity;
import static com.parasme.swopinfo.helper.Utils.openEmailClients;

/**
 * Created by :- Mukesh Kumawat on 02-Feb-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentCompany extends FragmentCompanyWrapper implements View.OnClickListener,FileSelectionActivity.FilePicker,Utils.MoveFileHelper, ImagePicker.Picker {

    private View childView;
    protected boolean isOwnCompany=false, isFirstRun = true;
    public static int companyId=0;
    public static JSONObject jsonObject;
    private TextView textCompanyAddress;
    private ShimmerTextView textCompanyTel1,textCompanyTel2,textCompanyFax,textCompanyMail;
    private String websiteURL="", fbURL="", twitterURL="";
    private ImageView imageWebsite,imageFacebook,imageTwitter, imageLocation;
    private String followLinkId;
    private LinearLayout uploadWrapperLayout;
    private ArrayList<Folder> folderArrayList = new ArrayList<>();
    public static ArrayList<File> fileArrayList = new ArrayList<>();
    public static TextView textFilesCount;
    private String businessEmail="";
    private GridView gridUploadsMultipleSelection;
    private String selectedFolderName="";
    private String businessAddress;

    @Override
    public void onResume() {
        super.onResume();

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        if(isFirstRun || baseLayout.getChildCount()==0) {
            isOwnCompany = this.getArguments().getBoolean("isOwnCompany", false);
            businessEmail = this.getArguments().getString("bussEmail");
            if (isOwnCompany) {
                companyId = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_OWN_COMPANY_ID, 0);
                textCompanyName.setOnClickListener(this);
            } else
                companyId = this.getArguments().getInt(AppConstants.KEY_COMPANY_ID);

            try {
                checkCompanyFollow(companyId + "");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String imageURL = AppConstants.URL_DOMAIN + "upload/company" + companyId + "/logo.jpg";
            Picasso.with(mActivity).load(imageURL)
                    .error(R.drawable.companylogo)
                    .placeholder(R.drawable.avtar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(imageCompany);
        }
            isFirstRun = false;
    }

    private void getComapnyDetails(String url) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Log.e("Company", "run: "+response );
                            jsonObject = new JSONObject(response);
                            textCompanyName.setText(jsonObject.optString("CompanyName"));
                            String address = jsonObject.optString("BussinessAdd1") + " "+ jsonObject.optString("BussinessAdd2")
                                    +"\n" + jsonObject.optString("BussinessCity") + "\n" + jsonObject.optString("BussinessCountry")
                                    +"\n" + jsonObject.optString("BussinessCode");

                            businessAddress = jsonObject.optString("BussinessAdd1") + " "+ jsonObject.optString("BussinessAdd2")+ " "+
                                    jsonObject.optString("BussinessCity")+" "+ jsonObject.optString("BussinessCode")+" "+
                                    jsonObject.optString("BussinessCountry");

                            Log.e("AADD", "run: "+address);
                            textCompanyAddress.setText(address);
                            textCompanyTel1.setText("Telephone 1: "+jsonObject.optString("BussinessTel1"));
                            textCompanyTel2.setText("Telephone 2: "+jsonObject.optString("BussinessTel2"));
                            textCompanyFax.setText("Fax: "+jsonObject.optString("BussinessFax"));
                            textCompanyMail.setText("Email: "+ jsonObject.optString("EmailAddress"));
                            websiteURL = jsonObject.optString("websitelink");
                            fbURL = jsonObject.optString("facebooklink");
                            twitterURL = jsonObject.optString("twitterlink");

                            // If visiting user is owner of company then hide follow button
                            if(jsonObject.optInt("ownerid") == SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID,0)){
                                btnFollow.setVisibility(View.GONE);
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        0,
                                        0.32f
                                );
                                textCompanyName.setLayoutParams(param);
                            }

                            setLinkListeners();

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

    private void setLinkListeners() {
        if(Patterns.WEB_URL.matcher(websiteURL).matches()){
            if(!websiteURL.startsWith("http"))
                websiteURL = "http://"+websiteURL;
            imageWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                    startActivity(intent);
                }
            });
        }

        if(fbURL.contains("facebook.com")){
            if(!fbURL.startsWith("http"))
                fbURL = "http://"+fbURL;
            imageFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(fbURL));
                    startActivity(intent);
                }
            });
        }

        if(twitterURL.contains("twitter.com")){
            if(!twitterURL.startsWith("http"))
                twitterURL = "http://"+twitterURL;

            imageTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(twitterURL));
                    startActivity(intent);
                }
            });
        }

        imageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentMap();
                Bundle bundle = new Bundle();
                bundle.putString("company",textCompanyName.getText().toString());
                bundle.putString("address",businessAddress);
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
            }
        });


        Shimmer shimmer = new Shimmer();
        Shimmer shimmer1 = new Shimmer();
        Shimmer shimmer2 = new Shimmer();
        Shimmer shimmer3 = new Shimmer();
        shimmer1.setDirection(1);
        shimmer3.setDirection(1);
        shimmer.start(textCompanyTel1);
        shimmer1.start(textCompanyTel2);
        shimmer2.start(textCompanyFax);
        shimmer3.start(textCompanyMail);

        textCompanyTel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telephone = textCompanyTel1.getText().toString();
                telephone = telephone.split(":")[1];
                openDialerActivity(mActivity,telephone);
            }
        });

        textCompanyTel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telephone = textCompanyTel2.getText().toString();
                telephone = telephone.split(":")[1];
                openDialerActivity(mActivity,telephone);
            }
        });

        textCompanyFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telephone = textCompanyFax.getText().toString();
                telephone = telephone.split(":")[1];
                openDialerActivity(mActivity,telephone);
            }
        });

        textCompanyMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailClients(mActivity, businessEmail);
            }
        });
    }


    private void prepareAboutView() {
        clearAllSelection();

        imageActionCompanyAbout.setImageResource(R.drawable.ic_info_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Company Details");
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_business_about, null);
        baseLayout.removeAllViews();
        baseLayout.addView(childView);

        textCompanyAddress = (TextView) childView.findViewById(R.id.textCompanyAddress);
        textCompanyTel1 = (ShimmerTextView) childView.findViewById(R.id.textCompanyTel1);
        textCompanyTel2 = (ShimmerTextView) childView.findViewById(R.id.textCompanyTel2);
        textCompanyFax = (ShimmerTextView) childView.findViewById(R.id.textCompanyFax);
        textCompanyMail = (ShimmerTextView) childView.findViewById(R.id.textCompanyMail);

        imageFacebook = (ImageView) childView.findViewById(R.id.image_fb);
        imageWebsite = (ImageView) childView.findViewById(R.id.image_web);
        imageTwitter = (ImageView) childView.findViewById(R.id.image_twitter);
        imageLocation = (ImageView) childView.findViewById(R.id.image_location);

        getComapnyDetails(AppConstants.URL_COMAPNY + companyId+"");

    }

    private void prepareUploadsWrapperView(){

        clearAllSelection();
        imageActionCompanyUploads.setImageResource(R.drawable.ic_uploads_active);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_grp_uploads_wrapper, null);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Uploads");

        View blankView1 = (View) childView.findViewById(R.id.blankView1);
        View blankView2 = (View) childView.findViewById(R.id.blankView2);
        Button buttonUploads = (Button) childView.findViewById(R.id.btnUploads);
        Button buttonFolders = (Button) childView.findViewById(R.id.btnFolders);
        ImageView imageAdd = (ImageView) childView.findViewById(R.id.btnImageAdd);

        buttonUploads.setOnClickListener(this);
        buttonFolders.setOnClickListener(this);
        imageAdd.setOnClickListener(this);

        buttonFolders.setVisibility(View.VISIBLE);
        if((SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID)+"").equals(companyId+"")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 0.32f;

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.weight = 0.02f;

            blankView2.setVisibility(View.VISIBLE);
            buttonUploads.setLayoutParams(params);
            buttonFolders.setLayoutParams(params);
            imageAdd.setLayoutParams(params);
            blankView1.setLayoutParams(params1);
            blankView2.setLayoutParams(params1);
        }
        else{
            imageAdd.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 0.48f;

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.weight = 0.04f;

            buttonUploads.setLayoutParams(params);
            buttonFolders.setLayoutParams(params);
            blankView1.setLayoutParams(params1);
        }

        baseLayout.removeAllViews();
        //layoutGrpUploadWrapper.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        prepareUploadsView(AppConstants.URL_COMAPNY + companyId + "/files");

        folderArrayList.clear();
        getFolders(AppConstants.URL_COMAPNY + companyId + "/folders", mActivity, null, null);

    }

    private void prepareFolderViews() {


        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_folders, null);
        uploadWrapperLayout = (LinearLayout) childView.findViewById(R.id.layoutUploadWrapper);

        GridView gridFolders = (GridView) view.findViewById(R.id.gridFolders);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        uploadWrapperLayout.removeAllViews();
        //uploadWrapperLayout.setGravity(Gravity.CENTER);
        uploadWrapperLayout.addView(view);

        gridFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prepareUploadsView(AppConstants.URL_COMAPNY + companyId + "/files/" + folderArrayList.get(i).getFolderName());
            }
        });

        // Load Company Folders once only
        if(!isCompanyFoldersLoaded){
            getFolders(AppConstants.URL_COMAPNY + companyId + "/folders", mActivity, gridFolders, emptyGridText);
            isCompanyFoldersLoaded = true;
        }
        else
            gridFolders.setAdapter(new FolderAdapter(mActivity,R.layout.item_folder,folderArrayList,true));
    }

    private void prepareAddView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);
        uploadWrapperLayout = (LinearLayout) childView.findViewById(R.id.layoutUploadWrapper);

        uploadWrapperLayout.removeAllViews();
        uploadWrapperLayout.setGravity(Gravity.CENTER);
        uploadWrapperLayout.addView(view);

        findFormViews(view);
    }

    private void findFormViews(View view) {
        final LinearLayout layoutSwop = (LinearLayout) view.findViewById(R.id.layoutSwop);
        final EditText editSwopText = (EditText) view.findViewById(R.id.editSwop);
        final EditText editFolderName = (EditText) view.findViewById(R.id.editFolderName);
        final EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
        final SocialAutoCompleteTextView editDescription = (SocialAutoCompleteTextView) view.findViewById(R.id.editDescription);
        editDescription.setHashtagColor(getResources().getColor(R.color.colorPrimary));
        editDescription.setMentionColor(getResources().getColor(R.color.colorPrimary));
        editDescription.setThreshold(1);


        final EditText editYoutubeLink = (EditText) view.findViewById(R.id.editYoutubeLink);
        final EditText editTag = (EditText) view.findViewById(R.id.editTag);
        final Spinner spinnerBroadcast = (Spinner) view.findViewById(R.id.spinnerBroadcast);
        final CheckBox checkBoxComments = (CheckBox) view.findViewById(R.id.checkBoxComments);
        final CheckBox checkBoxRatings = (CheckBox) view.findViewById(R.id.checkBoxRatings);
        final CheckBox checkBoxEmbedded = (CheckBox) view.findViewById(R.id.checkBoxEmbedded);
        final CheckBox checkBoxDownloads = (CheckBox) view.findViewById(R.id.checkBoxDownloads);
        final Button btnUpload = (Button) view.findViewById(R.id.btnUpload);
        final Button btnSelectFile = (Button) view.findViewById(R.id.btnSelectFile);
        textFilesCount = (TextView) view.findViewById(R.id.textFilesCount);

        editFolderName.setVisibility(View.VISIBLE);
        layoutSwop.setVisibility(View.GONE);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,broadcastArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroadcast.setAdapter(adapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentAdd().uploadClick(editSwopText,editFolderName,editTitle,editDescription,editYoutubeLink,editTag,spinnerBroadcast,checkBoxComments,checkBoxRatings,checkBoxEmbedded,checkBoxDownloads,mActivity,AppConstants.USER_ID,companyId+"","0",fileArrayList);
            }
        });

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils(mActivity).selectDialog(new String[]{"Camera","Gallery","Files"},FragmentCompany.this, FragmentCompany.this);
            }
        });

    }

    private void prepareMembersView() {
        clearAllSelection();

        imageActionCompanyMembers.setImageResource(R.drawable.ic_following_acive);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_followers, null);

        GridView gridFollower = (GridView) childView.findViewById(R.id.gridFollowers);
        TextView emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Members");

        baseLayout.removeAllViews();
        //baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        String url = AppConstants.URL_COMAPNY + companyId +"/members";
        try {
            new FragmentFollowers().getFollowers(url, mActivity, gridFollower, emptyGridText,true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareFollowersView() {
        clearAllSelection();

        imageActionCompanyFollowers.setImageResource(R.drawable.ic_followers_active);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_followers, null);

        GridView gridFollower = (GridView) childView.findViewById(R.id.gridFollowers);
        TextView emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Followers");

        baseLayout.removeAllViews();
        //baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        String url = AppConstants.URL_FOLLOWING + companyId +"/companyfollowers";
        try {
            new FragmentFollowers().getFollowers(url, mActivity, gridFollower, emptyGridText,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textCompanyName:
                if(companyId == SharedPreferenceUtility.getInstance().get(AppConstants.PREF_OWN_COMPANY_ID,0))
                    MainActivity.replaceFragment(new FragmentCompanyEdit(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);
                break;

            case R.id.btnFollow:
                follow();
                break;

            case R.id.imageActionCompanyAbout:
                prepareAboutView();
                break;

            case R.id.imageActionCompanyUploads:
                prepareUploadsWrapperView();
                break;

            case R.id.imageActionCompanyMembers:
                prepareMembersView();
                break;

            case R.id.imageActionCompanyFollowers:
                prepareFollowersView();
                break;

            case R.id.btnFolders:
                setDefaultViews();
                ((Button) childView.findViewById(R.id.btnFolders)).setBackgroundResource(R.drawable.btn_bg_active);
                prepareFolderViews();
                break;

            case R.id.btnUploads:
                setDefaultViews();
                ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_active);
                prepareUploadsView(AppConstants.URL_COMAPNY + companyId + "/files");
                break;

            case R.id.btnImageAdd:
                setDefaultViews();
                ((ImageView) childView.findViewById(R.id.btnImageAdd)).setBackgroundResource(R.drawable.btn_bg_active);
                prepareAddView();
                break;

            case R.id.imageCompany:
                if(companyId == SharedPreferenceUtility.getInstance().get(AppConstants.PREF_OWN_COMPANY_ID,0))
                    MainActivity.replaceFragment(new FragmentCompanyEdit(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);
                break;
        }
    }

    private void checkCompanyFollow(final String companyId) throws IOException {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("check company", response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.equals("null") || response == null || response.equals(null))
                                btnFollow.setText("Follow");
                            else {
                                btnFollow.setText("Unfollow");
                                JSONObject jsonObject = new JSONObject(response);
                                followLinkId = jsonObject.optInt("FollowLinkID") + "";
                            }

                            prepareAboutView();

                        } catch (JSONException e) {
                        }


                    }
                });
            }
        };
        String url = AppConstants.URL_FOLLOWING + AppConstants.USER_ID + "/IsFollowingCompany/" + companyId;
        webServiceHandler.get(url);

    }

    private void follow() {
        try {

            if (btnFollow.getText().toString().equalsIgnoreCase("follow"))
                followCompany(AppConstants.USER_ID, companyId+"");
            else
                unFollowCompany(AppConstants.USER_ID, companyId+"");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void followCompany(String userId, String companyId) throws IOException {
        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"ownerid","followingid","FollowLinkID","Type","ReceiveEmailNotifications"},new String[]{userId,companyId,"0","2","true"});

        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("IsFollowing Resp",response);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("{")){
                            btnFollow.setText("Unfollow");

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                followLinkId = jsonObject.optInt("FollowLinkID") + "";
                            }catch (JSONException e){}

                            MyApplication.alertDialog(mActivity,"You have successfully followed this Company","Follow Success");
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not follow","Alert");
                    }
                });



            }
        };
        webServiceHandler.post(AppConstants.URL_FOLLOWING,builder);
    }

    private void unFollowCompany(String userId, String companyId) throws IOException{
        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"ownerid","followingid","FollowLinkID","Type"},new String[]{userId,companyId,followLinkId,"2"});
        Log.e("DATA",userId+"_____"+companyId+"_____"+followLinkId+"_____1");
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Unfollow Resp",response);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("SUCCESS")){
                            btnFollow.setText("Follow");
                            MyApplication.alertDialog(mActivity,"You have successfully unfollowed this company","Unfollow Success");
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not unfollow","Alert");
                    }
                });
            }
        };
        webServiceHandler.delete(AppConstants.URL_FOLLOWING,builder);
    }

    private void prepareUploadsView(String url) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);
        uploadWrapperLayout = (LinearLayout) childView.findViewById(R.id.layoutUploadWrapper);

        gridUploadsMultipleSelection = (GridView) view.findViewById(R.id.gridUploads);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        uploadWrapperLayout.removeAllViews();
        //uploadWrapperLayout.setGravity(Gravity.CENTER);
        uploadWrapperLayout.addView(view);

        adapter = new UploadAdapter(mActivity, R.layout.item_upload, uploadArrayList);
        gridUploadsMultipleSelection.setAdapter(adapter);

        // Only when user is owner of company
        if(companyId == ((int) SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID))) {
            // Method to set multi selection mode to gridview
            Utils.moveFileHelper = FragmentCompany.this;
            Utils.setGridMultiSelection(mActivity, gridUploadsMultipleSelection, adapter);
        }

        if(!isCompanyFilesLoaded){
            new FragmentUploads().getUploads(url, AppConstants.USER_UPLOADS, gridUploadsMultipleSelection, emptyGridText, mActivity);
            if(!url.contains("Files/"))
                isCompanyFilesLoaded = true;
        }
        else
            gridUploadsMultipleSelection.setAdapter(adapter);

        gridUploadsMultipleSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("fildeId",uploadArrayList.get(i).getFileId());
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

    }
    public void getFolders(String url, final Activity activity, final GridView gridView, final TextView emptyText) {
        if(folderArrayList.size()==0){

            folderArrayList.clear();
            WebServiceHandler webServiceHandler=new WebServiceHandler(activity);
            webServiceHandler.serviceListener=new WebServiceListener() {
                @Override
                public void onResponse(final String response) {
                    Log.e("FODERS",response);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONArray jsonArray=new JSONArray(response);
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.optJSONObject(i);

                                    Folder folder=new Folder();
                                    folder.setFolderName(jsonObject.optString("DirectoryName"));
                                    folder.setNumOfFiles(jsonObject.optInt("TotalFiles")+"");
                                    folder.setUserId(companyId);

                                    folderArrayList.add(folder);
                                }

                                if(gridView!=null) {
                                    gridView.setAdapter(new FolderAdapter(activity, R.layout.item_folder, folderArrayList, true));
                                    gridView.setEmptyView(emptyText);
                                }
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
        else{
            gridView.setAdapter(new FolderAdapter(activity,R.layout.item_folder,folderArrayList,true));
            gridView.setEmptyView(emptyText);
        }
    }


    @Override
    public void onFilesSelected(ArrayList<File> resultFileList) {
        fileArrayList = resultFileList;

        Log.e("asfdsaf",resultFileList.get(0).getPath());
        int size = fileArrayList.size();
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText((size==1) ? "1 File Selected" : (size+" Files Selected"));
    }

    @Override
    public void onImagePicked(Bitmap bitmap, String imagePath) {
        fileArrayList.clear();
        File file = new File(imagePath);
        fileArrayList.add(file);
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText("1 File Selected");
    }

    @Override
    public void onVideoPicked(String videoPath) {
        fileArrayList.clear();
        File file = new File(videoPath);
        fileArrayList.add(file);
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText("1 Video Selected");
    }

    @Override
    public void onFileMoveButtonClicked(final Object[] object) {
        final Dialog dialogMove =((Dialog)object[0]);
        dialogMove.show();

        final GridView gridFolders = ((GridView)object[2]);
        final TextView textMove = ((TextView) object[1]);
        Button btnCancel = ((Button) object[3]);
        Button btnMove = ((Button)object[4]);

        gridFolders.setAdapter(new FolderAdapter(mActivity,R.layout.item_folder,folderArrayList,true));
        gridFolders.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        gridFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Grid", "onItemClick: "+folderArrayList.get(i).getFolderName());

                selectedFolderName = folderArrayList.get(i).getFolderName();
                textMove.setText("Moving File(s) to "+ selectedFolderName);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMove.dismiss();
            }
        });

        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMove.dismiss();
                moveFiles((ArrayList<String>) object[5], selectedFolderName);
            }
        });

    }

    private void moveFiles(ArrayList<String> fileIds, final String targetFolderName){
        ArrayList<String> paramsList = new ArrayList<>();
        ArrayList<String> valuesList = new ArrayList<>();

        for (int i = 0; i < fileIds.size(); i++) {
            paramsList.add("fileids");
            valuesList.add(fileIds.get(i));
        }

        paramsList.add("newfolder");
        valuesList.add(targetFolderName);

        String[] paramsArray = new String[paramsList.size()];
        String[] valuesArray = new String[valuesList.size()];

        paramsArray = paramsList.toArray(paramsArray);
        valuesArray = valuesList.toArray(valuesArray);

        FormBody.Builder builder = WebServiceHandler.createBuilder(paramsArray, valuesArray);
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Move Resp", "onResponse: "+response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")) {
//                                setDefaultViews();
//                                btnFolders.setBackgroundResource(R.drawable.btn_bg_active);
//                                prepareFolderViews();
                                MyApplication.alertDialog(mActivity, "File(s) are successfully moved to " + targetFolderName, "Move Files");
                            }
                            else
                                MyApplication.alertDialog(mActivity,"File(s) moving unsuccessful", "Move Files");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_FILE + "movefiles",builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultViews() {
        ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_inactive);
        ((Button) childView.findViewById(R.id.btnFolders)).setBackgroundResource(R.drawable.btn_bg_inactive);
        ((ImageView) childView.findViewById(R.id.btnImageAdd)).setBackgroundResource(R.drawable.btn_bg_inactive);
    }

    //                        URI uri = (URI) getIntent().getExtras().get(Intent.EXTRA_STREAM);

}