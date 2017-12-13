package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.TouchImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentSharedPromotion extends Fragment {
    private AppCompatActivity mActivity;
    private TouchImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.promotion_pager_item, container, false);
        findViews(view);

        String url = this.getArguments().getString("promotion");
        url = url.replaceAll(" ","%20");
        Picasso.with(mActivity).load(url).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).into(imageView);

        return view;
    }



    private void findViews(View view) {
        imageView  =(TouchImageView) view.findViewById(R.id.image_single_promotion);
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

    @Override
    public void onResume() {
        super.onResume();
    }

}