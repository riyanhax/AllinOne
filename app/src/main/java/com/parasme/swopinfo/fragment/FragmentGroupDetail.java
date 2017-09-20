package com.parasme.swopinfo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.applozic.mobicomkit.channel.service.ChannelClientService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelAddMemberTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FollowerAdapter;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Follow;
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
import static com.parasme.swopinfo.fragment.FragmentUploads.adapter;
import static com.parasme.swopinfo.fragment.FragmentUploads.uploadArrayList;

/**
 * Created by :- Mukesh Kumawat on 11-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentGroupDetail extends FragmentGroupWrapper implements FileSelectionActivity.FilePicker, ImagePicker.Picker{

    private String groupId,groupOwnerId,groupLinkId,groupName;
    private View childView;
    private LinearLayout uploadWrapperLayout;
    private ArrayList<Follow> memberArrayList =new ArrayList<>();
    private boolean isJoined, isFirstRun = true;
    private Button buttonUploads;
    private ImageView imageAdd;
    public static ArrayList<File> fileArrayList=new ArrayList<>();
    public static TextView textFilesCount;
    private boolean isChangingPicture;
    private String groupURLToShare="";
    public boolean requireAccessCode, readOnlyGroup;
    private String groupAccessCode;
    private Dialog dialogAccessCode;

    @Override
    public void onResume() {
        super.onResume();

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        groupId = this.getArguments().getString(AppConstants.KEY_GROUP_ID);
//        groupOwnerId = this.getArguments().getString(AppConstants.KEY_USER_ID);
//        groupName = this.getArguments().getString(AppConstants.KEY_GROUP_NAME);
        groupURLToShare = AppConstants.URL_DOMAIN+"groupprofile.aspx?token="+groupId;

        if (isFirstRun) {
            checkJoinStatus(AppConstants.USER_ID, groupId);
        }
        isFirstRun = false;
    }

    private void setGroupImageAndName() {
        textGroupName.setText(groupName);
        String url=AppConstants.URL_DOMAIN+"upload/group"+groupId+"/logo.jpg";
        Picasso.with(mActivity).load(url)
                .error(R.drawable.app_icon)
                .placeholder(R.drawable.app_icon)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(imageGroup);

        if(groupOwnerId.equals(AppConstants.USER_ID)){
            imageGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChangingPicture = true;
                    ImagePicker.picker = FragmentGroupDetail.this;
                    startActivity(new Intent(mActivity,ImagePicker.class));
                }
            });

            btnDelete.setVisibility(View.VISIBLE);
            layoutGroupShare.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mActivity);
                    adb.setMessage("Are you sure to delete the group?");
                    adb.setTitle("Delete Group");
                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteGroup(AppConstants.URL_GROUP + "delete");
                        }
                    });
                    adb.setNegativeButton("No", null);
                    adb.show();
                }
            });
        }
        else{
            btnDelete.setVisibility(View.GONE);
            imageGroup.setOnClickListener(null);
        }

    }

    private void deleteGroup(String url) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Del Group", "onResponse: "+response );
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("SUCCESS")){
                            deleteChannelAppLozic(groupId, groupOwnerId);
                            //getFragmentManager().popBackStack();
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not delete the group","Delete Group");
                    }
                });
            }
        };
        try {
            webServiceHandler.post(url,WebServiceHandler.createBuilder(new String[]{"groupid"}, new String[]{groupId} ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteChannelAppLozic(String clientGroupId, String groupOwnerId) {
        String url = "https://apps.applozic.com/rest/ws/group/delete?clientGroupId="+clientGroupId+"&ofUserId="+groupOwnerId;
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("DELETECHANEL", "onResponse: "+response );
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            getFragmentManager().popBackStack();
/*
                            if(jsonObject.optString("status").equalsIgnoreCase("success"))
                                getFragmentManager().popBackStack();
                            else
                                MyApplication.alertDialog(mActivity, "Could not delete group", "Delete Group");
*/

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.getAppLozic(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkJoinStatus(final String userId, final String groupId) {
        String url=AppConstants.URL_GROUP + userId + "/IsGroupMember/" + groupId;
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("JOINSTATUS",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            groupOwnerId = jsonObject.optInt("OwnerUserID")+"";
                            groupName = jsonObject.optString("groupname");
                            requireAccessCode = jsonObject.optBoolean("requiresaccesscode");
                            readOnlyGroup = jsonObject.optBoolean("readonlygroup");
                            groupAccessCode = jsonObject.optString("groupaccesscode");

                            if(jsonObject.optInt("groupid")!=0){
                                groupLinkId=jsonObject.optInt("grouplinkid")+"";
                                btnAction.setText("Leave");
                                isJoined=true;
                                checkGroupOnApplozic(groupId);
                            }
                            else {
                                btnAction.setText("Join");
                                isJoined=false;
                            }

                            if((jsonObject.optInt("OwnerUserID")+"").equals(userId)){
                                btnAction.setVisibility(View.GONE);
                                isJoined=true;
                            }
                            setGroupImageAndName();
                            prepareGrpUploadsWrapper();

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

    private void checkGroupOnApplozic(String groupId) {
        String url = "https://apps.applozic.com/rest/ws/group/v2/info?clientGroupId="+groupId;
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Applzcgrp",response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.optString("status").contains("error"))
                                btnChat.setVisibility(View.VISIBLE);
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.getAppLozic(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareGrpUploadsWrapper() {
        imageActionGroupMembers.setImageResource(R.drawable.ic_followers);
        imageActionGroupUploads.setImageResource(R.drawable.ic_uploads_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Group");
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_grp_uploads_wrapper, null);

        buttonUploads = (Button) childView.findViewById(R.id.btnUploads);
        imageAdd = (ImageView) childView.findViewById(R.id.btnImageAdd);

        // If group is not joined then hiding + add button showing only uploads
        if (!isJoined || (readOnlyGroup && !groupOwnerId.equals(AppConstants.USER_ID)))
            hideAddUploadButton();

        buttonUploads.setOnClickListener(this);
        imageAdd.setOnClickListener(this);

        baseLayout.removeAllViews();
        //layoutGrpUploadWrapper.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        prepareUploadsView();
    }

    private void hideAddUploadButton() {
        imageAdd.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        buttonUploads.setLayoutParams(params);
    }

    private void prepareUploadsView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);
        uploadWrapperLayout = (LinearLayout) childView.findViewById(R.id.layoutUploadWrapper);

        GridView gridUploads = (GridView) view.findViewById(R.id.gridUploads);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        uploadWrapperLayout.removeAllViews();
        uploadWrapperLayout.setGravity(Gravity.CENTER);
        uploadWrapperLayout.addView(view);

        String url = AppConstants.URL_GROUP + groupId + "/Files";

        adapter = new UploadAdapter(mActivity, R.layout.item_upload, uploadArrayList);
        gridUploads.setAdapter(adapter);

        new FragmentUploads().getUploads(url,AppConstants.GROUP_UPLOADS,gridUploads,emptyGridText,mActivity);

        gridUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putInt("fildeId",uploadArrayList.get(i).getFileId());
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
            }
        });
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
        final EditText editDescription = (EditText) view.findViewById(R.id.editDescription);
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

        editFolderName.setVisibility(View.GONE);
        layoutSwop.setVisibility(View.GONE);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,broadcastArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroadcast.setAdapter(adapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentAdd().uploadClick(editSwopText,editFolderName,editTitle,editDescription,editYoutubeLink,editTag,spinnerBroadcast,checkBoxComments,checkBoxRatings,checkBoxEmbedded,checkBoxDownloads,mActivity,AppConstants.USER_ID,"0",groupId,fileArrayList);
            }
        });

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils(mActivity).selectDialog(new String[]{"Camera","Gallery","Files"},FragmentGroupDetail.this, FragmentGroupDetail.this);
//                FileSelectionActivity.setOnFilePickListener(FragmentGroupDetail.this);
//                startActivity(new Intent(mActivity,FileSelectionActivity.class));
            }
        });

    }

    private void prepareGrpMembersView() {
        imageActionGroupUploads.setImageResource(R.drawable.ic_upload);
        imageActionGroupMembers.setImageResource(R.drawable.ic_followers_active);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Group");
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_followers, null);

        GridView gridView = (GridView) childView.findViewById(R.id.gridFollowers);
        TextView emptyText = (TextView) childView.findViewById(R.id.emptyGridText);

        baseLayout.removeAllViews();
        //layoutGrpUploadWrapper.setGravity(Gravity.CENTER);
        baseLayout.addView(childView);

        String url=AppConstants.URL_GROUP + groupId+"/Members";
        try {
            getMembers(url,gridView,emptyText);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void getMembers(String url, final GridView gridView, final TextView emptyGridText) throws IOException {
        memberArrayList.clear();
        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.e("Members Resp",response);

                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.optJSONObject(i);
                        Follow follow =new Follow();
                        follow.setUserId(jsonObject.optInt("OwnerUserID"));
                        follow.setFollowingId(jsonObject.optInt("userid"));
                        follow.setGroupId(jsonObject.optInt("groupid"));
                        follow.setGroupLinkId(jsonObject.optInt("grouplinkid"));
                        follow.setTimeDate(jsonObject.optString("timedate"));
                        follow.setFullName(jsonObject.optString("membername"));

                        memberArrayList.add(follow);
                    }

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setAdapter(new FollowerAdapter(mActivity,R.layout.item_follower, memberArrayList,false));
                            gridView.setEmptyView(emptyGridText);

                        }
                    });

                }catch (JSONException e){}
            }
        };
        webServiceHandler.get(url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUploads:
                ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_active);
                ((ImageView) childView.findViewById(R.id.btnImageAdd)).setBackgroundResource(R.drawable.btn_bg_inactive);
                prepareUploadsView();
                break;
            case R.id.btnImageAdd:
                ((Button) childView.findViewById(R.id.btnUploads)).setBackgroundResource(R.drawable.btn_bg_inactive);
                ((ImageView) childView.findViewById(R.id.btnImageAdd)).setBackgroundResource(R.drawable.btn_bg_active);
                prepareAddView();
                break;
            case R.id.imageActionGroupUploads:
                prepareGrpUploadsWrapper();
                break;
            case R.id.imageActionGroupMembers:
                prepareGrpMembersView();
                break;
            case R.id.btnAction:
                actionClick();
                break;
            case R.id.btnLinkedIn:
                new Utils(mActivity).sharePost("com.linkedin.android","Linkedin",groupURLToShare);
                break;
            case R.id.btnMail:
                new Utils(mActivity).shareOnMails(groupURLToShare);
                break;
            case R.id.btnGPlus:
                new Utils(mActivity).sharePost("com.google.android.apps.plus","Google Plus",groupURLToShare);
                break;
            case R.id.btnFacebook:
                new Utils(mActivity).sharePost("com.facebook.katana","Facebook",groupURLToShare);
                break;
            case R.id.btnTwitter:
                new Utils(mActivity).sharePost("com.twitter.android","Twitter",groupURLToShare);
                break;
            case R.id.btnWhatsapp:
                new Utils(mActivity).sharePost("com.whatsapp","Whatsapp",groupURLToShare);
                break;
            case R.id.btnPinterest:
                new Utils(mActivity).sharePost("com.pinterest","Pinterest",groupURLToShare);
                break;
            case R.id.btnChat:
                Intent intent = new Intent(mActivity, ConversationActivity.class);
                intent.putExtra(ConversationUIService.CLIENT_GROUP_ID, groupId);
                startActivity(intent);
                break;
        }
    }

    private void actionClick(){
        if(btnAction.getText().toString().equalsIgnoreCase("join")){
            if(!requireAccessCode)
                joinGroup(groupId,AppConstants.USER_ID);
            else{
                loadGroupCodeDialog();
                dialogAccessCode.show();
            }
        }
        else{
            leaveGroup(groupId,groupLinkId,AppConstants.USER_ID);
        }
    }

    private void joinGroup(final String groupId, final String userId){
        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"grouplinkid","groupid","userid"},new String[]{"0",groupId,userId});

        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("GROUPJOIN", "onResponse: "+response );
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.contains("{")){
                            try{

                                JSONObject jsonObject=new JSONObject(response);
                                groupLinkId=jsonObject.optInt("grouplinkid")+"";
                                btnAction.setText("Leave");
                                imageAdd.setVisibility(View.VISIBLE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.weight = 0.49f;
                                buttonUploads.setLayoutParams(params);

                                new AsyncAddMemberAppLozic().execute(groupId, userId);
                            }
                            catch (JSONException e){e.printStackTrace();}
                        }
                        else
                            MyApplication.alertDialog(mActivity,"Could not join","Alert");
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_JOIN_GROUP,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String addMemberToChannelProcess(String clientGroupId, String userId) {
        if (TextUtils.isEmpty(clientGroupId) && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = ChannelClientService.getInstance(mActivity).addMemberToChannel(clientGroupId, userId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse.getStatus();
    }


    private void leaveGroup(final String groupId, String groupLinkId, final String userId){
        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"grouplinkid","groupid","userid"},new String[]{groupLinkId,groupId,userId});

        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.equalsIgnoreCase("failed"))
                            MyApplication.alertDialog(mActivity,"Could not leave","Alert");
                        else{
                            btnAction.setText("Join");
                            imageAdd.setVisibility(View.GONE);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.weight = 1f;
                            buttonUploads.setLayoutParams(params);

                            new AsyncRemoveMemberAppLozic().execute(groupId, userId);
                        }
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_LEAVE_GROUP,builder);
        } catch (IOException e) {
            e.printStackTrace();
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
    public void onImagePicked(final Bitmap bitmap, final String imagePath) {
        if(!isChangingPicture){
            fileArrayList.clear();
            File file = new File(imagePath);
            fileArrayList.add(file);
            textFilesCount.setVisibility(View.VISIBLE);
            textFilesCount.setText("1 File Selected");
        }
        else{
            isChangingPicture = false;
            AlertDialog.Builder adb = new AlertDialog.Builder(mActivity);
            adb.setMessage("Update group profile picture with selected image ?");
            adb.setTitle("Update Group Profile");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updatePic(bitmap,imagePath);
                }
            });
            adb.setNegativeButton("No", null);
            adb.show();

        }
    }

    @Override
    public void onVideoPicked(String videoPath) {
        fileArrayList.clear();
        File file = new File(videoPath);
        fileArrayList.add(file);
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText("1 Video Selected");

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
                                MyApplication.alertDialog(mActivity,"Group profile picture updated successfully","Update Picture");
                                imageGroup.setImageBitmap(bitmap);
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not update group picture","Alert");

                        }catch (JSONException e){}
                    }
                });
            }
        };
        try{
            webServiceHandler.postMultiPart(AppConstants.URL_GROUP + groupId + "/profilepic" , WebServiceHandler.createMultiPartBuilder(new String[]{"profilepic"},new String[]{imagePath}),AppConstants.USER_ID);
        }catch (IOException e){}
    }

    private void loadGroupCodeDialog() {
        final EditText editAccessCode;
        Button buttonJoin;

        dialogAccessCode = new Dialog(mActivity);
        dialogAccessCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAccessCode.setContentView(R.layout.dialog_group_access_code);

        editAccessCode = (EditText) dialogAccessCode.findViewById(R.id.editAccessCode);
        buttonJoin = (Button) dialogAccessCode.findViewById(R.id.btnDialogJoin);

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = editAccessCode.getText().toString();
                if(accessCode.equals("")){
                    editAccessCode.setError("Enter Access Code");
                    editAccessCode.requestFocus();
                    return;
                }

                if(accessCode.equals(groupAccessCode)){
                    dialogAccessCode.dismiss();
                    joinGroup(groupId,AppConstants.USER_ID);
                }
                else{
                    editAccessCode.setError("Incorrect Access Code");
                    editAccessCode.requestFocus();
                    return;
                }
            }
        });
    }

    public class AsyncAddMemberAppLozic extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String data="null";
            addMemberToChannelProcess(params[0], params[1]);
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            progressDialog.dismiss();
            MyApplication.alertDialog(mActivity,"You have successfully joined this group","Join Success");
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Adding To Group");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public class AsyncRemoveMemberAppLozic extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... params) {
            String data="null";
            removeMemberFromChannelProcess(params[0], params[1]);
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            progressDialog.dismiss();
            MyApplication.alertDialog(mActivity,"You have successfully left this group","Leave Success");
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Leaving Group");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public String removeMemberFromChannelProcess(String clientGroupId, String userId) {
        if (TextUtils.isEmpty(clientGroupId) && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = ChannelClientService.getInstance(mActivity).leaveMemberFromChannel(clientGroupId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse.getStatus();

    }

}