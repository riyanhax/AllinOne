package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;

/**
 * Created by :- Mukesh Kumawat on 10-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public abstract class FragmentUserWrapper extends Fragment implements View.OnClickListener {
    protected View baseView;
    protected AppCompatActivity mActivity;
    protected ImageView imageActionUserFeeds,imageActionUserUploads,imageActionUserFollowing,imageActionUserFollower,imageActionUserGroups;
    protected LinearLayout baseLayout;
    protected ImageView imageUser;
    protected Button btnFollow,btnCard,btnChat;
    protected TextView textUserName,textCompany,textTitle,textUploadViews, textUploadDownloads, textProfileViews;

    protected boolean isUserFilesLoaded, isUserFollowersLoaded, isUserFollowingLoaded, isUserGroupsLoaded, isUserFoldersLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_user_wrapper, container, false);

        imageActionUserFeeds = (ImageView) baseView.findViewById(R.id.imageActionFeed);
        imageActionUserUploads = (ImageView) baseView.findViewById(R.id.imageActionUploads);
        imageActionUserFollowing = (ImageView) baseView.findViewById(R.id.imageActionFollowing);
        imageActionUserFollower = (ImageView) baseView.findViewById(R.id.imageActionFollower);
        imageActionUserGroups = (ImageView) baseView.findViewById(R.id.imageActionGroups);
        baseLayout = (LinearLayout) baseView.findViewById(R.id.layout);
        imageUser = (ImageView) baseView.findViewById(R.id.imageUser);
        btnFollow = (Button) baseView.findViewById(R.id.btnFollow);
        textUserName = (TextView) baseView.findViewById(R.id.textUserName);
        textCompany = (TextView) baseView.findViewById(R.id.textCompany);
        textTitle = (TextView) baseView.findViewById(R.id.textTitle);
        textUploadViews = (TextView) baseView.findViewById(R.id.textUploadViews);
        textUploadDownloads = (TextView) baseView.findViewById(R.id.textUploadDownloads);
        textProfileViews = (TextView) baseView.findViewById(R.id.textProfileViews);
        btnCard = (Button) baseView.findViewById(R.id.btnViewCard);
        btnChat = (Button) baseView.findViewById(R.id.btnChat);

        imageActionUserFeeds.setOnClickListener(this);
        imageActionUserUploads.setOnClickListener(this);
        imageActionUserFollowing.setOnClickListener(this);
        imageActionUserFollower.setOnClickListener(this);
        imageActionUserGroups.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        textCompany.setOnClickListener(this);
        textTitle.setOnClickListener(this);
        btnCard.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        imageUser.setOnClickListener(this);
        return baseView;
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

        if (activity instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) activity;
        }
    }

    protected void clearAllSelection(){
        imageActionUserFollower.setImageResource(R.drawable.ic_followers);
        imageActionUserUploads.setImageResource(R.drawable.ic_upload);
        imageActionUserFeeds.setImageResource(R.drawable.ic_newsfeed);
        imageActionUserFollowing.setImageResource(R.drawable.ic_following);
        imageActionUserGroups.setImageResource(R.drawable.ic_groups);
    }

}