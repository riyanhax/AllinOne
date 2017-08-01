package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.FollowingAdapter;
import com.parasme.swopinfo.application.AppConstants;
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

public class FragmentFollowing extends BaseFragment {

    private GridView gridFollwoing;
    private TextView emptyGridText;
    public static ArrayList<Follow> followingArrayList =new ArrayList<>();
    private View childView;

    public void getFollowing(String userId, final Activity activity, final GridView gridView, final TextView emptyText) throws IOException {
        followingArrayList.clear();
        String url= AppConstants.URL_FOLLOWING + userId;

        WebServiceHandler webServiceHandler=new WebServiceHandler(activity);
        webServiceHandler.get(url);

        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Followig Rspn",response);

                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.optJSONObject(i);
                        Follow follow =new Follow();
                        follow.setUserId(jsonObject.optInt("OwnerID"));
                        follow.setFollowingId(jsonObject.optInt("FollowingID"));
                        follow.setFollowLinkId(jsonObject.optInt("FollowLinkID"));
                        follow.setType(jsonObject.optInt("Type"));
                        follow.setTimeDate(jsonObject.optString("TimeDate"));
                        follow.setUserName(jsonObject.optString("username"));
                        follow.setUserEmail(jsonObject.optString("userEmail"));
                        follow.setFullName(jsonObject.optString("Name"));
                        follow.setReceiveNotification(jsonObject.optBoolean("ReceiveEmailNotifications"));
                        follow.setReceiveNotificationUser(jsonObject.optBoolean("ReceiveEmailNotificationsUser"));

                        followingArrayList.add(follow);
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setAdapter(new FollowingAdapter(activity,R.layout.item_followings, followingArrayList));
                            gridView.setEmptyView(emptyText);
                        }
                    });
                }catch (JSONException e){}
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        imageActionFollowing.setImageResource(R.drawable.ic_following_acive);

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_following, null);

        gridFollwoing = (GridView)  childView.findViewById(R.id.gridFollwoing);
        emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Following");

        LinearLayout layout = (LinearLayout) baseView.findViewById(R.id.layout);
        layout.setGravity(Gravity.CENTER);
        layout.addView(childView);

        try {
            String userId=this.getArguments().getString(AppConstants.KEY_USER_ID);

            // Preventing re loading followers
            if(!isFollowingLoaded){
                getFollowing(userId,mActivity,gridFollwoing,emptyGridText);
                    isFollowingLoaded = true;
            }
            else
                gridFollwoing.setAdapter(new FollowingAdapter(mActivity,R.layout.item_followings,followingArrayList));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}