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
 * Created by :- Mukesh Kumawat on 02-Feb-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public abstract class FragmentCompanyWrapper extends Fragment implements View.OnClickListener {

    protected View baseView;
    protected AppCompatActivity mActivity;
    protected LinearLayout baseLayout;
    protected TextView textCompanyName;
    protected ImageView imageCompany,imageActionCompanyAbout,imageActionCompanyUploads,imageActionCompanyMembers,imageActionCompanyFollowers;
    protected Button btnFollow;
    protected boolean isCompanyFilesLoaded,isCompanyFoldersLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_company_wrapper, container, false);
        baseLayout = (LinearLayout) baseView.findViewById(R.id.layout);
        textCompanyName = (TextView) baseView.findViewById(R.id.textCompanyName);
        imageCompany = (ImageView) baseView.findViewById(R.id.imageCompany);
        imageActionCompanyAbout = (ImageView) baseView.findViewById(R.id.imageActionCompanyAbout);
        imageActionCompanyUploads = (ImageView) baseView.findViewById(R.id.imageActionCompanyUploads);
        imageActionCompanyMembers = (ImageView) baseView.findViewById(R.id.imageActionCompanyMembers);
        imageActionCompanyFollowers = (ImageView) baseView.findViewById(R.id.imageActionCompanyFollowers);
        imageCompany = (ImageView) baseView.findViewById(R.id.imageCompany);
        btnFollow = (Button) baseView.findViewById(R.id.btnFollow);

        btnFollow.setOnClickListener(this);
        imageActionCompanyAbout.setOnClickListener(this);
        imageActionCompanyUploads.setOnClickListener(this);
        imageActionCompanyMembers.setOnClickListener(this);
        imageActionCompanyFollowers.setOnClickListener(this);
        imageCompany.setOnClickListener(this);
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

        if (activity instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) activity;
        }
    }

    protected void clearAllSelection(){
        imageActionCompanyAbout.setImageResource(R.drawable.ic_info);
        imageActionCompanyUploads.setImageResource(R.drawable.ic_upload);
        imageActionCompanyMembers.setImageResource(R.drawable.ic_following);
        imageActionCompanyFollowers.setImageResource(R.drawable.ic_followers);
    }
}
