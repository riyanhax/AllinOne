package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.GroupAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Group;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;


/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentGroups extends BaseFragment {

    private View childView;
    private GridView gridGroups;
    private CircleImageView floatingAddGroup;
    private TextView emptyGridText;
    public Dialog dialogGroupAdd;
    //    public static String groupId="";
    public static ArrayList<Group> groupArrayList =new ArrayList<>();
    public GroupAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();

        imageActionGroups.setImageResource(R.drawable.ic_groups_active);


        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Groups");

        childView = mActivity.getLayoutInflater().inflate(R.layout.fragment_groups, null);
        gridGroups = (GridView) childView.findViewById(R.id.gridGroups);
        floatingAddGroup = (CircleImageView) childView.findViewById(R.id.imgAddGroup);
        emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);

        LinearLayout layout = (LinearLayout) baseView.findViewById(R.id.layout);
        layout.addView(childView);

        loadDialog();

        String userId=this.getArguments().getString(AppConstants.KEY_USER_ID);

        if(!isGroupsLoaded) {
            getGroups(userId, gridGroups, mActivity, emptyGridText);
                isGroupsLoaded = true;
        }
        else{
            gridGroups.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        floatingAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogGroupAdd.show();
            }
        });
        gridGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_GROUP_ID,groupArrayList.get(i).getGroupId()+"");
                bundle.putString(AppConstants.KEY_GROUP_NAME,groupArrayList.get(i).getGroupName());
                bundle.putString(AppConstants.KEY_USER_ID,groupArrayList.get(i).getOwnerUserId()+"");
                Fragment fragment = new FragmentGroupDetail();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
            }
        });
    }

    private void loadDialog(){
        final EditText editGroupName,editGroupAccessCode;
        final RadioButton radioAccessCode,radioReadOnly;
        Button buttonAddGroup;

        dialogGroupAdd = new Dialog(mActivity);
        dialogGroupAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogGroupAdd.setContentView(R.layout.dialog_group_create);

        editGroupName=(EditText) dialogGroupAdd.findViewById(R.id.editGroupName);
        editGroupAccessCode=(EditText) dialogGroupAdd.findViewById(R.id.editAccessCode);
        radioAccessCode=(RadioButton) dialogGroupAdd.findViewById(R.id.radioAccessCode);
        radioReadOnly=(RadioButton) dialogGroupAdd.findViewById(R.id.radioReadOnly);
        buttonAddGroup=(Button) dialogGroupAdd.findViewById(R.id.buttonAddGroup);

        radioAccessCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b)
                    editGroupAccessCode.setVisibility(View.VISIBLE);
                else
                    editGroupAccessCode.setVisibility(View.GONE);
            }
        });

        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime = sdf.format(new Date());

                String groupName= EmojiHandler.encodeJava(editGroupName.getText().toString());

                String groupAccessCode=editGroupAccessCode.getText().toString();
                boolean isAccessCodeNeeded=radioAccessCode.isChecked();
                boolean isReadOnly=radioReadOnly.isChecked();

                if(groupName.equals("")){
                    editGroupName.setError("Please enter group name");
                    editGroupName.requestFocus();
                    return;
                }
                if(!isAccessCodeNeeded && groupAccessCode.equals("")){
                    editGroupAccessCode.setError("Please enter group access code");
                    editGroupAccessCode.requestFocus();
                    return;
                }
                if(groupAccessCode.equals(""))
                    groupAccessCode="null";
                dialogGroupAdd.dismiss();
                createGroup(groupName, AppConstants.USER_ID,currentDateandTime,!isAccessCodeNeeded+"",groupAccessCode,!isReadOnly+"");
            }
        });
    }


    public void createGroup(final String groupName, final String userId, String currentDateandTime, String requireAccessCode, String groupAccessCode, String isReadOnly) {
        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"groupid","groupname","groupowner","grouplogo","timedate","requiresaccesscode","groupaccesscode","readonlygroup"},new String[]{"0",groupName,userId,"false",currentDateandTime,requireAccessCode,groupAccessCode,isReadOnly});
        Log.e("DATA",groupName+"__"+userId+"__"+currentDateandTime+"__"+requireAccessCode+"__"+groupAccessCode+"___"+isReadOnly);
        WebServiceHandler webServiceHandler=new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Create Groupres",response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            addNewGroupToCurrentList(jsonObject, userId, groupName, jsonObject.optInt("groupid"));
                        }   catch (JSONException e){e.printStackTrace();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),"Unable to create group",Snackbar.LENGTH_LONG).show();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_CREATE_GROUP,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewGroupToCurrentList(JSONObject jsonObject, String userId, String groupName, int groupId) {
        Group group=new Group();
        group.setGroupId(jsonObject.optInt("groupid"));
        group.setOwnerUserId(Integer.parseInt(userId));
        group.setTotalFiles(jsonObject.optInt("TotalFiles"));
        group.setTotalMembers(jsonObject.optInt("TotalMembers"));
        group.setGroupName(jsonObject.optString("groupname"));
        group.setTimeDate(jsonObject.optString("timedate"));
        group.setOwnerFullName(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME) + " " + SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME));
        group.setOwnerUserName(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_NAME)+"");
        groupArrayList.add(group);
        adapter.notifyDataSetChanged();
        new AsyncGroupAppLozic().execute(groupName, groupId+"");
    }


    public void getGroups(String userId, final GridView gridView, final Activity activity, final TextView emptyText){
        adapter = new GroupAdapter(activity,R.layout.item_group,groupArrayList);
        gridView.setAdapter(adapter);
        groupArrayList.clear();
        String url=AppConstants.URL_USER_GROUP + userId;
        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Group Response",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.optJSONObject(i);
                                Group group=new Group();
                                group.setGroupLinkId(jsonObject.optInt("grouplinkid"));
                                group.setGroupId(jsonObject.optInt("groupid"));
                                group.setOwnerUserId(jsonObject.optInt("OwnerUserID"));
                                group.setTotalFiles(jsonObject.optInt("TotalFiles"));
                                group.setTotalMembers(jsonObject.optInt("TotalMembers"));
                                group.setGroupName(EmojiHandler.decodeJava(jsonObject.optString("groupname")));
                                group.setTimeDate(jsonObject.optString("timedate"));
                                group.setOwnerUserName(EmojiHandler.decodeJava(jsonObject.optString("OwnerUserName")));
                                group.setOwnerFullName(EmojiHandler.decodeJava(jsonObject.optString("OwnerName")));
                                group.setOwnerEmail(jsonObject.optString("memberemail"));
                                group.setGroupAccessCode(jsonObject.optString("groupaccesscode"));
                                group.setGroupLogo(jsonObject.optBoolean("grouplogo"));
                                group.setRequireAccessCode(jsonObject.optBoolean("requiresaccesscode"));
                                group.setReadOnlyGroup(jsonObject.optBoolean("readonlygroup"));
                                groupArrayList.add(group);
                            }
                            adapter.notifyDataSetChanged();
                            gridView.setEmptyView(emptyText);
                        }catch (JSONException e){e.printStackTrace();}
                        catch (NullPointerException e){e.printStackTrace();}
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

    private void addGroupToAppLozic(String groupName, String groupId){
        Log.e("clientgroupid",groupId);
        List<String> channelMembersList =  new ArrayList<String>();
/*
        channelMembersList.add("user1");
        channelMembersList.add("user2");
        channelMembersList.add("user3");//Note:while creating group exclude logged in userId from list
*/
        ChannelInfo channelInfo  = new ChannelInfo(groupName,channelMembersList);
        channelInfo.setType(Channel.GroupType.PUBLIC.getValue().intValue()); //group type
        //channelInfo.setImageUrl(""); //pass group image link URL
        //channelInfo.setChannelMetadata(channelMetadata); //Optional option for setting group meta data
        channelInfo.setClientGroupId(groupId); //Optional if you have your own groupId then you can pass here
        Channel channel = ChannelService.getInstance(getActivity()).createChannel(channelInfo);
        Log.e("CHANNEL",channel.getName());
    }

    public class AsyncGroupAppLozic extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(String... params) {
            String data="null";
            addGroupToAppLozic(params[0], params[1]);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Adding Group");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}