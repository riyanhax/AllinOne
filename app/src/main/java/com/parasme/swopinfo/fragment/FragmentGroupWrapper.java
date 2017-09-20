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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parasme.swopinfo.R;

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
    protected FloatingActionButton btnLinkedIn,btnMail,btnFacebook,btnTwitter,btnGPlus,btnPinterest,btnWhatsapp;

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
        btnLinkedIn = (FloatingActionButton) baseView.findViewById(R.id.btnLinkedIn);
        btnMail = (FloatingActionButton) baseView.findViewById(R.id.btnMail);
        btnFacebook = (FloatingActionButton) baseView.findViewById(R.id.btnFacebook);
        btnTwitter = (FloatingActionButton) baseView.findViewById(R.id.btnTwitter);
        btnGPlus = (FloatingActionButton) baseView.findViewById(R.id.btnGPlus);
        btnPinterest = (FloatingActionButton) baseView.findViewById(R.id.btnPinterest);
        btnWhatsapp = (FloatingActionButton) baseView.findViewById(R.id.btnWhatsapp);

        imageActionGroupUploads.setOnClickListener(this);
        imageActionGroupMembers.setOnClickListener(this);
        btnAction.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnGPlus.setOnClickListener(this);
        btnLinkedIn.setOnClickListener(this);
        btnMail.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnPinterest.setOnClickListener(this);
        btnWhatsapp.setOnClickListener(this);
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