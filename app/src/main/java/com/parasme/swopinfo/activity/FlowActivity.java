package com.parasme.swopinfo.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.FlowPagerAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;

import java.util.ArrayList;


/**
 * Created by SoNu on 3/29/2017.
 */

public class FlowActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<Integer> flowImagesList = new ArrayList<>();
    private ImageView imageLeft,imageRight;
    private Button btnProceed;
    private RelativeLayout layoutActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        imageLeft = (ImageView) findViewById(R.id.image_left);
        imageRight = (ImageView) findViewById(R.id.image_right);
        btnProceed = (Button) findViewById(R.id.btnProceed);
        layoutActions = (RelativeLayout) findViewById(R.id.layoutActions);

        flowImagesList.add(R.drawable.img_intro_1);
        flowImagesList.add(R.drawable.img_intro_2);
        flowImagesList.add(R.drawable.img_intro_3);
        FlowPagerAdapter adapter = new FlowPagerAdapter(FlowActivity.this, flowImagesList);
        viewPager.setAdapter(adapter);

        imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
            }
        });

        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem()==2){
                    SharedPreferenceUtility.getInstance().save(AppConstants.PREF_INTRO,true);
                    new FragmentUser().getUserDetails(AppConstants.URL_USER + SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID),true,FlowActivity.this);
                }
                else
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_INTRO,true);
                new FragmentUser().getUserDetails(AppConstants.URL_USER + SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID),true,FlowActivity.this);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if(position==0)
                    imageLeft.setVisibility(View.GONE);
                else
                    imageLeft.setVisibility(View.VISIBLE);

                if(position==2){
                    layoutActions.setVisibility(View.GONE);
                    btnProceed.setVisibility(View.VISIBLE);
                }
                else{
                    layoutActions.setVisibility(View.VISIBLE);
                    btnProceed.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}