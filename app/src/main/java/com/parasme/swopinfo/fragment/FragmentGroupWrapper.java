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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by :- Mukesh Kumawat on 11-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public abstract class FragmentGroupWrapper extends Fragment implements View.OnClickListener {

    protected View baseView;
    protected AppCompatActivity mActivity;
    protected ImageView imageGroup,imageActionGroupUploads,imageActionGroupMembers;
    protected TextView textGroupName;
    protected Button btnAction,btnDelete,btnChat;
    protected LinearLayout baseLayout;
    protected RelativeLayout layoutGroupShare;
    protected CircleImageView imgShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_group_wrapper, container, false);
        imageGroup = (ImageView) baseView.findViewById(R.id.imageGroup);
        imageActionGroupUploads = (ImageView) baseView.findViewById(R.id.imageActionGroupUploads);
        imageActionGroupMembers = (ImageView) baseView.findViewById(R.id.imageActionGroupMembers);
        textGroupName = (TextView) baseView.findViewById(R.id.textGroupName);
        btnAction = (Button) baseView.findViewById(R.id.btnAction);
        btnDelete = (Button) baseView.findViewById(R.id.btnDelete);
        btnChat = (Button) baseView.findViewById(R.id.btnChat);
        baseLayout = (LinearLayout) baseView.findViewById(R.id.layout);
        layoutGroupShare = (RelativeLayout) baseView.findViewById(R.id.layoutGroupShare);
        imgShare = (CircleImageView) baseView.findViewById(R.id.imgShare);

        imageActionGroupUploads.setOnClickListener(this);
        imageActionGroupMembers.setOnClickListener(this);
        btnAction.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        imgShare.setOnClickListener(this);
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

}