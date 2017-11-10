package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.Touch;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LoginActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.activity.TouchImageView;
import com.parasme.swopinfo.adapter.FolderAdapter;
import com.parasme.swopinfo.adapter.FollowerAdapter;
import com.parasme.swopinfo.adapter.FollowingAdapter;
import com.parasme.swopinfo.adapter.GroupAdapter;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Follow;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;

import static com.parasme.swopinfo.activity.MainActivity.replaceFragment;
import static com.parasme.swopinfo.fragment.FragmentFolders.folderArrayList;
import static com.parasme.swopinfo.fragment.FragmentFollowers.followArrayList;
import static com.parasme.swopinfo.fragment.FragmentFollowing.followingArrayList;
import static com.parasme.swopinfo.fragment.FragmentUploads.adapter;
import static com.parasme.swopinfo.fragment.FragmentUploads.uploadArrayList;

/**
 * Created by :- Mukesh Kumawat on 11-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentUser extends FragmentUserWrapper implements SwipeRefreshLayout.OnRefreshListener {

    private View childView;
    private String userId;
    private String followLinkId = "", followUserName = "";
    private LinearLayout uploadWrapperLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private Follow userDetails;
    private JSONObject jsonUserObject;

    @Override
    public void onResume() {
        super.onResume();

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        userId = this.getArguments().getString(AppConstants.KEY_USER_ID);
        followUserName = this.getArguments().getString(AppConstants.KEY_USER_NAME);
        Log.e("USERNAME",followUserName);

        setUserData();

        try {
            checkUserFollow(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUserData() {
        textUserName.setText(followUserName);
        String imageURL = AppConstants.URL_DOMAIN+"upload/user" + userId + "/profilepic.jpg";
        Picasso.with(mActivity).load(imageURL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageUser);

    }

    private void prepareFollowingView() {
        clearAllSelection();
        imageActionUserFollowing.setImageResource(R.drawable.ic_following_acive);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_following, null);

        GridView gridFollwoing = (GridView) childView.findViewById(R.id.gridFollwoing);
        TextView emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Following");

        baseLayout.removeAllViews();
        //baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        try {
            // Load followings of user only once
            if(!isUserFollowingLoaded){
                new FragmentFollowing().getFollowing(userId, mActivity, gridFollwoing, emptyGridText);
                isUserFollowingLoaded = true;
            }
            else
                gridFollwoing.setAdapter(new FollowingAdapter(mActivity,R.layout.item_followings,followingArrayList));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareFeedView() {
        clearAllSelection();

        imageActionUserFeeds.setImageResource(R.drawable.ic_newsfeed_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Newsfeed");

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);
        ((LinearLayout) childView.findViewById(R.id.layoutOwnProfile)).setVisibility(View.GONE);
        ((ImageView) childView.findViewById(R.id.img_checkin)).setVisibility(View.GONE);
        listView = (ListView) childView.findViewById(R.id.listFeeds);
        baseLayout.removeAllViews();
        //baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        swipeRefreshLayout = (SwipeRefreshLayout) childView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new FragmentHome().getFeeds(mActivity, listView, swipeRefreshLayout);
            }
        });
    }


    private void prepareFollowerView() {
        clearAllSelection();

        imageActionUserFollower.setImageResource(R.drawable.ic_followers_active);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_followers, null);

        GridView gridFollower = (GridView) childView.findViewById(R.id.gridFollowers);
        TextView emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Followers");

        baseLayout.removeAllViews();
        //baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        String url = AppConstants.URL_FOLLOWERS + userId;
        try {
            // Load followers of user only once
            if(!isUserFollowersLoaded){
                new FragmentFollowers().getFollowers(url, mActivity, gridFollower, emptyGridText,false);
                isUserFollowersLoaded = true;
            }
            else
                gridFollower.setAdapter(new FollowerAdapter(mActivity,R.layout.item_follower,followArrayList,false));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareGroupsView() {
        clearAllSelection();

        imageActionUserGroups.setImageResource(R.drawable.ic_groups_active);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_groups, null);

        GridView gridGroups = (GridView) childView.findViewById(R.id.gridGroups);
        TextView emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        FloatingActionButton floatingAddGroup = (FloatingActionButton) childView.findViewById(R.id.floatingAddGroup);
        floatingAddGroup.setVisibility(View.GONE);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Groups");

        baseLayout.removeAllViews();
        baseLayout.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        gridGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_GROUP_ID, FragmentGroups.groupArrayList.get(i).getGroupId() + "");
                bundle.putString(AppConstants.KEY_GROUP_NAME, FragmentGroups.groupArrayList.get(i).getGroupName());
                bundle.putString(AppConstants.KEY_USER_ID, FragmentGroups.groupArrayList.get(i).getOwnerUserId() + "");
                Fragment fragment = new FragmentGroupDetail();
                fragment.setArguments(bundle);
                replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        // Load groups of user only once
        if(!isUserGroupsLoaded){
            new FragmentGroups().getGroups(userId, gridGroups, mActivity, emptyGridText);
            isUserGroupsLoaded = true;
        }
        else
            gridGroups.setAdapter(new GroupAdapter(mActivity,R.layout.item_group,FragmentGroups.groupArrayList));

    }

    private void prepareUpldWrapperView() {
        clearAllSelection();

        imageActionUserUploads.setImageResource(R.drawable.ic_uploads_active);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_grp_uploads_wrapper, null);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Uploads");

        Button buttonUploads = (Button) childView.findViewById(R.id.btnUploads);
        Button btnFolders = (Button) childView.findViewById(R.id.btnFolders);
        ((ImageView) childView.findViewById(R.id.btnImageAdd)).setVisibility(View.GONE);
        buttonUploads.setOnClickListener(this);
        btnFolders.setVisibility(View.VISIBLE);
        btnFolders.setOnClickListener(this);

        baseLayout.removeAllViews();
        //layoutGrpUploadWrapper.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        prepareUploadsView(AppConstants.URL_USER + userId + "/files");
    }

    private void prepareUploadsView(String url) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);
        uploadWrapperLayout = (LinearLayout) childView.findViewById(R.id.layoutUploadWrapper);

        GridView gridUploads = (GridView) view.findViewById(R.id.gridUploads);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        uploadWrapperLayout.removeAllViews();
        //uploadWrapperLayout.setGravity(Gravity.CENTER);
        uploadWrapperLayout.addView(view);

        adapter = new UploadAdapter(mActivity, R.layout.item_upload, uploadArrayList);
        gridUploads.setAdapter(adapter);

        // To prevent api calling each time for visiting uplaods for same user
        if(!isUserFilesLoaded) {
            new FragmentUploads().getUploads(url, AppConstants.USER_UPLOADS, gridUploads, emptyGridText, mActivity);
            if(!url.contains("Files/"))     // To load uploads api when returning from folders files
                isUserFilesLoaded = true;
        }
        else
            gridUploads.setAdapter(adapter);

        gridUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("fildeId",uploadArrayList.get(i).getFileId());
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

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
                prepareUploadsView(AppConstants.URL_USER + userId + "/Files/" + folderArrayList.get(i).getFolderName());
            }
        });


        // Load folders of user only once
        if(!isUserFoldersLoaded){
            folderArrayList.clear();
            new FragmentFolders().getFolders(userId, mActivity, gridFolders, emptyGridText);
            isUserFoldersLoaded = true;
        }
        else
            gridFolders.setAdapter(new FolderAdapter(mActivity,R.layout.item_folder,folderArrayList,false));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageActionFeed:
                textUploadViews.setVisibility(View.GONE);
                textUploadDownloads.setVisibility(View.GONE);
                prepareFeedView();
                break;
            case R.id.imageActionFollowing:
                textUploadViews.setVisibility(View.GONE);
                textUploadDownloads.setVisibility(View.GONE);
                prepareFollowingView();
                break;
            case R.id.imageActionFollower:
                textUploadViews.setVisibility(View.GONE);
                textUploadDownloads.setVisibility(View.GONE);
                prepareFollowerView();
                break;
            case R.id.imageActionGroups:
                textUploadViews.setVisibility(View.GONE);
                textUploadDownloads.setVisibility(View.GONE);
                prepareGroupsView();
                break;
            case R.id.imageActionUploads:
                textUploadViews.setVisibility(View.VISIBLE);
                textUploadDownloads.setVisibility(View.VISIBLE);
                prepareUpldWrapperView();
                break;
            case R.id.btnUploads:
                ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_active);
                ((Button) childView.findViewById(R.id.btnFolders)).setBackgroundResource(R.drawable.btn_bg_inactive);
                prepareUploadsView(AppConstants.URL_USER + userId + "/files");
                break;
            case R.id.btnFolders:
                ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_inactive);
                ((Button) childView.findViewById(R.id.btnFolders)).setBackgroundResource(R.drawable.btn_bg_active);
                prepareFolderViews();
                break;
            case R.id.btnFollow:
                follow();
                break;
            case R.id.textCompany:
                Fragment fragment = new FragmentCompany();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isOwnCompany", false);
                bundle.putInt(AppConstants.KEY_COMPANY_ID,userDetails.getCompanyId());
                fragment.setArguments(bundle);
                replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
                break;
            case R.id.btnViewCard:
                Fragment fragment1=new FragmentCard();
                Bundle bundle1 = new Bundle();
                bundle1.putString(AppConstants.KEY_USER_ID, userId);
                fragment1.setArguments(bundle1);
                replaceFragment(fragment1,getFragmentManager(),mActivity,R.id.content_frame);
                break;
            case R.id.textTitle:
                Fragment fragment2 = new FragmentCompany();
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isOwnCompany", false);
                bundle2.putInt(AppConstants.KEY_COMPANY_ID,userDetails.getCompanyId());
                fragment2.setArguments(bundle2);
                replaceFragment(fragment2,getFragmentManager(),mActivity,R.id.content_frame);
                break;
            case R.id.btnChat:
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, userId);
                intent.putExtra(ConversationUIService.DISPLAY_NAME, followUserName); //put it for displaying the title.
                intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
                startActivity(intent);
                break;
            case R.id.imageUser:
                showUserDetailDialog();
                break;
        }
    }

    private void showUserDetailDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_user_detail);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        findAndSetViews(dialog);
    }

    private void findAndSetViews(final Dialog dialog) {
        TextView textUserName = (TextView) dialog.findViewById(R.id.text_username);
        TextView textEmail = (TextView) dialog.findViewById(R.id.text_email);
        TextView textCountry = (TextView) dialog.findViewById(R.id.text_country);
        TextView textProfileViews = (TextView) dialog.findViewById(R.id.text_profile_views);
        TextView textFileViews = (TextView) dialog.findViewById(R.id.text_file_views);
        TextView textDownloads = (TextView) dialog.findViewById(R.id.text_download_views);
        TextView textBussTitle = (TextView) dialog.findViewById(R.id.text_buss_title);
        TextView textCompanyName = (TextView) dialog.findViewById(R.id.text_buss_company);
        TextView textBussEmail = (TextView) dialog.findViewById(R.id.text_buss_email);
        TextView textBussCell = (TextView) dialog.findViewById(R.id.text_buss_cell);
        TextView textBussTel = (TextView) dialog.findViewById(R.id.text_buss_tel);
        TextView textBussFax = (TextView) dialog.findViewById(R.id.text_buss_fax);
        TouchImageView imageUser = (TouchImageView) dialog.findViewById(R.id.image_user);
        ImageView imageClose = (ImageView) dialog.findViewById(R.id.image_close);
        LinearLayout layoutBussDetails = (LinearLayout) dialog.findViewById(R.id.layout_buss_details);

        String imageUrl = AppConstants.URL_DOMAIN+"upload/user"+ jsonUserObject.optInt("userid")+"/profilepic.jpg";
        Picasso.with(mActivity).load(imageUrl)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageUser);

        textUserName.setText(jsonUserObject.optString("userFirstname")+" "+jsonUserObject.optString("userLastname"));
        textEmail.setText(jsonUserObject.optString("userEmail"));
        textCountry.setText(jsonUserObject.optString("country"));
        textProfileViews.setText(jsonUserObject.optInt("ProfileViews")+"");
        textFileViews.setText(jsonUserObject.optInt("TotalFileViews")+"");
        textDownloads.setText(jsonUserObject.optInt("TotalFileDownloads")+"");

        if(!jsonUserObject.isNull("businessTitle")) {
            layoutBussDetails.setVisibility(View.VISIBLE);
            textBussTitle.setText(jsonUserObject.optString("businessTitle"));
            textCompanyName.setText(jsonUserObject.optString("businessCompanyName"));
            textBussEmail.setText(jsonUserObject.optString("businessEmail"));
            textBussCell.setText(jsonUserObject.optString("businessCell"));
            textBussTel.setText(jsonUserObject.optString("businessDirecttel"));
            textBussFax.setText(jsonUserObject.optString("businesscustomfax"));
        }

        imageUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void checkUserFollow(final String followingId) throws IOException {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("user check", response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.equals("null") || response == null || response.equals(null))
                                btnFollow.setText("Follow");
                            else {
                                btnFollow.setText("Unfollow");
                                btnChat.setVisibility(View.VISIBLE);
                                JSONObject jsonObject = new JSONObject(response);
                                followLinkId = jsonObject.optInt("FollowLinkID") + "";
                            }

                            getUserDetails(AppConstants.URL_USER + followingId,false,mActivity);

                        } catch (JSONException e) {
                        }


                    }
                });
            }
        };

        String url = AppConstants.URL_FOLLOWING + AppConstants.USER_ID + "/IsFollowing/" + followingId;
        webServiceHandler.get(url);

    }

    private void follow() {
        try {

            if (btnFollow.getText().toString().equalsIgnoreCase("follow"))
                followUser(AppConstants.USER_ID, userId);
            else
                unFollowUser(AppConstants.USER_ID, userId);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void followUser(String userId, String followingId) throws IOException {
        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"ownerid","followingid","FollowLinkID","Type","ReceiveEmailNotifications"},new String[]{userId,followingId,"0","1","true"});

        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Follow Resp",response);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("{")){
                            btnFollow.setText("Unfollow");
                            btnChat.setVisibility(View.VISIBLE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                followLinkId = jsonObject.optInt("FollowLinkID") + "";
                            }catch (JSONException e){}

                            MyApplication.alertDialog(mActivity,"You have successfully followed this user","Follow Success");
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not follow","Alert");
                    }
                });



            }
        };
        webServiceHandler.post(AppConstants.URL_FOLLOWING,builder);
    }


    private void unFollowUser(String userId, String followingId) throws IOException{
        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"ownerid","followingid","FollowLinkID","Type"},new String[]{userId,followingId,followLinkId,"1"});
        Log.e("DATA",userId+"_____"+followingId+"_____"+followLinkId+"_____1");
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Unfollow Resp",response);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("200")){
                            btnFollow.setText("Follow");
                            btnChat.setVisibility(View.GONE);
                            MyApplication.alertDialog(mActivity,"You have successfully unfollowed this user","Unfollow Success");
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not unfollow","Alert");
                    }
                });
            }
        };
        webServiceHandler.post(AppConstants.URL_FOLLOWING+"delete",builder);
    }

    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        new FragmentHome().getFeeds(mActivity, listView,swipeRefreshLayout);
    }

    public void getUserDetails(String url, final boolean fromSplash, final Activity activity){
        WebServiceHandler webServiceHandler = new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Details", "onResponse: "+response );

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonUserObject = new JSONObject(response).optJSONObject("returnvalue");
                            userDetails = new Follow();
                            if (jsonUserObject == null) {
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                                activity.finish();
                            }

                            else{
                            userDetails.setUserEmail(jsonUserObject.optString("userEmail"));
                            userDetails.setBusinessEmail(jsonUserObject.optString("businessEmail"));
                            userDetails.setCompanyId(jsonUserObject.optInt("companyid"));
                            userDetails.setBusinessTitle(jsonUserObject.optString("businessTitle"));
                            userDetails.setBusinessStatus(jsonUserObject.optString("businessstatus"));
                            userDetails.setCompanyName(jsonUserObject.optString("businessCompanyName"));
                            userDetails.setBusinessCell(jsonUserObject.optString("businessCell"));
                            userDetails.setBusinessTel(jsonUserObject.optString("businessDirecttel"));
                            userDetails.setBusinessFax(jsonUserObject.optString("businesscustomfax"));

                            if (!fromSplash) {
                                textUploadViews.setText("Views: " + jsonUserObject.optString("TotalFileViews"));
                                textUploadDownloads.setText("Downloads: " + jsonUserObject.optString("TotalFileDownloads"));
                                textProfileViews.setText("Profile Views: " + jsonUserObject.optString("ProfileViews"));

                                if (jsonUserObject.optInt("companyid") == 0) {
                                    btnCard.setVisibility(View.GONE);
                                    textCompany.setVisibility(View.GONE);
                                    textTitle.setVisibility(View.GONE);
                                } else {
                                    textTitle.setText(userDetails.getBusinessTitle() + " at");
                                    textCompany.setText(userDetails.getCompanyName());
                                }
                                prepareFeedView();
                            } else {
                                AppConstants.UPLOAD_VIEWS = jsonUserObject.optString("TotalFileViews");
                                AppConstants.UPLOAD_DOWNLOADS = jsonUserObject.optString("TotalFileDownloads");
                                AppConstants.PROFILE_VIEWS = jsonUserObject.optString("ProfileViews");
                                Intent i = new Intent(activity, MainActivity.class);
                                i.putExtra("startUserWrapper", false);
                                activity.startActivity(i);
                                activity.finish();
                            }
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
}