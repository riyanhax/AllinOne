package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.FollowerAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.model.Follow;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentFollowers extends BaseFragment {

    private View childView;
    private GridView gridFollowers;
    private TextView emptyGridText;
    public static ArrayList<Follow> followArrayList =new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();

        imageActionFollower.setImageResource(R.drawable.ic_followers_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Followers");
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_followers, null);

        gridFollowers = (GridView)  childView.findViewById(R.id.gridFollowers);
        emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);

        LinearLayout layout = (LinearLayout) baseView.findViewById(R.id.layout);
        //layout.setGravity(Gravity.CENTER);
        layout.addView(childView);

        String userId=this.getArguments().getString(AppConstants.KEY_USER_ID);
        String url=AppConstants.URL_FOLLOWERS + userId;

        try {

            // Preventing re loading followings
            if(!isFollowersLoaded){
                isFollowersLoaded=true;
                getFollowers(url,mActivity,gridFollowers,emptyGridText,false);
            }
            else
                gridFollowers.setAdapter(new FollowerAdapter(mActivity,R.layout.item_follower,followArrayList,false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFollowers(String url, final Activity activity, final GridView gridView, final TextView emptyText, final boolean isCompanyMember) throws IOException {
        followArrayList.clear();
        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.e("Follwoer Resp",response);

                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.optJSONObject(i);
                        Follow follow =new Follow();
                        follow.setUserId(jsonObject.optInt("OwnerID"));

                        if(isCompanyMember)
                            follow.setUserId(jsonObject.optInt("userid"));

                        follow.setFollowingId(jsonObject.optInt("FollowingID"));
                        follow.setFollowLinkId(jsonObject.optInt("FollowLinkID"));
                        follow.setType(jsonObject.optInt("Type"));
                        follow.setTimeDate(jsonObject.optString("TimeDate"));
                        follow.setUserName(jsonObject.optString("username"));
                        follow.setUserEmail(jsonObject.optString("userEmail"));
                        follow.setFullName(EmojiHandler.decodeJava(jsonObject.optString("Name")));

                        // When loading company members
                        if(jsonObject.has("membername"))
                            follow.setFullName(EmojiHandler.decodeJava(jsonObject.optString("membername")));


                        follow.setReceiveNotification(jsonObject.optBoolean("ReceiveEmailNotifications"));
                        follow.setReceiveNotificationUser(jsonObject.optBoolean("ReceiveEmailNotificationsUser"));

                        followArrayList.add(follow);
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setAdapter(new FollowerAdapter(activity,R.layout.item_follower, followArrayList,isCompanyMember));
                            gridView.setEmptyView(emptyText);
                        }
                    });

                }catch (JSONException e){}
            }
        };
        webServiceHandler.get(url);
    }
}