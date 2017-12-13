package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentFollowing;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.model.Follow;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

/**
 * Created by SoNu on 6/6/2016.
 */

public class FollowingAdapter extends ArrayAdapter<Follow> {

    private int resourceId;
    private ArrayList<Follow> followingArrayList;
    private Context context;
    private ViewHolder viewHolder;
    LayoutInflater vi;

    public FollowingAdapter(Context context, int resourceId, ArrayList<Follow> followingArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.followingArrayList = followingArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        Log.e("SIZE", followingArrayList.size()+"");
        return followingArrayList.size();
    }


    class ViewHolder
    {
        ImageView imageFollowingUser;
        TextView textFollowingUser;
        CheckBox checkBoxNotification;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.imageFollowingUser = (ImageView) view.findViewById(R.id.image_following);
            viewHolder.textFollowingUser = (TextView) view.findViewById(R.id.textFollwingName);
            viewHolder.checkBoxNotification = (CheckBox) view.findViewById(R.id.checkBoxFollowing);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String url=AppConstants.URL_DOMAIN+"upload/user"+ followingArrayList.get(position).getFollowingId()+"/profilepic.jpg";
        if(followingArrayList.get(position).getType()==2)
            url = AppConstants.URL_DOMAIN+"upload/company"+followingArrayList.get(position).getFollowingId()+"/logo.jpg";
        Picasso.with(context).load(url)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(viewHolder.imageFollowingUser);

        viewHolder.textFollowingUser.setText(followingArrayList.get(position).getFullName());

        if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentFollowing){
            if(followingArrayList.get(position).isReceiveNotification()){
                viewHolder.checkBoxNotification.setChecked(true);
            }
            else {
                viewHolder.checkBoxNotification.setChecked(false);
            }

            viewHolder.checkBoxNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isChecked = followingArrayList.get(position).isReceiveNotification();
                    followingArrayList.get(position).setReceiveNotification(!isChecked);
                    Log.e("aaaaa", "before: "+isChecked );
                    Log.e("bbbbb", "after: "+!isChecked );
                    String follwingId =  followingArrayList.get(position).getFollowingId()+"";
                    String followLinkId =  followingArrayList.get(position).getFollowLinkId()+"";
                    try {
                        if(isChecked)
                            toggleGetNotification(AppConstants.USER_ID,follwingId,"0",!isChecked);
                        else
                            toggleGetNotification(AppConstants.USER_ID,follwingId,followLinkId,!isChecked);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        else
            viewHolder.checkBoxNotification.setVisibility(View.GONE);


        viewHolder.imageFollowingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentUser();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID,followingArrayList.get(position).getFollowingId()+"");
                bundle.putString(AppConstants.KEY_USER_NAME,followingArrayList.get(position).getFullName());
                fragment.setArguments(bundle);

                if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUser)
                {
                    Intent i = new Intent((Activity) context,MainActivity.class);
                    i.putExtra("startUserWrapper",true);
                    i.putExtra(AppConstants.KEY_USER_ID, followingArrayList.get(position).getFollowingId()+"");
                    i.putExtra(AppConstants.KEY_USER_NAME, followingArrayList.get(position).getFullName());
                    ((Activity) context).startActivity(i);
                    ((Activity) context).finish();
                }
                else
                    MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);

            }
        });

        return view;
    }

    private void toggleGetNotification(String userId, String followingId, String followLinkId, final boolean isChecked) throws IOException {
        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"ownerid","followingid","FollowLinkID","Type","ReceiveEmailNotifications"},new String[]{userId,followingId,followLinkId,"1",""+isChecked});

        WebServiceHandler webServiceHandler =  new WebServiceHandler(context);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Rec Notif Resp1",response);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isChecked)
                            MyApplication.alertDialog((Activity)context,"You will now receive notification for selected user","Receive Notification");
                        else
                            MyApplication.alertDialog((Activity)context,"You will not receive any notification for selected user","Receive Notification");

                    }
                });
            }
        };

        webServiceHandler.post(AppConstants.URL_FOLLOWING,builder);
    }
}