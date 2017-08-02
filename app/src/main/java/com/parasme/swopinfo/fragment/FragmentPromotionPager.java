package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.PromotionPagerAdapter;
import com.parasme.swopinfo.model.Store;

import java.util.ArrayList;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentPromotionPager extends Fragment {

    private AppCompatActivity mActivity;
    private ViewPager pagerPromotions;
    private ArrayList<Store.Promotion> promotionArrayList;
    private MenuItem itemSearch, itemHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_promotion_pager, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        promotionArrayList = new ArrayList<>();
        setArrayList();

        pagerPromotions.setAdapter(new PromotionPagerAdapter(mActivity, promotionArrayList));

        return view;
    }

    private void setArrayList() {
        TypedArray gamePromos = getResources().obtainTypedArray(R.array.game);
        TypedArray hifiPromos = getResources().obtainTypedArray(R.array.hifi);
        TypedArray pickPromos = getResources().obtainTypedArray(R.array.pick);
        TypedArray sparPromos = getResources().obtainTypedArray(R.array.spar);
        TypedArray woolPromos = getResources().obtainTypedArray(R.array.wool);

        switch (FragmentRetailerLogos.retailerPosition){
            case 0:
                for (int i = 0; i <gamePromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(gamePromos.getResourceId(i,0)));
                }
                break;
            case 1:
                for (int i = 0; i <hifiPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(hifiPromos.getResourceId(i,0)));
                }
                break;
            case 2:
                for (int i = 0; i <pickPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(pickPromos.getResourceId(i,0)));
                }
                break;
            case 3:
                for (int i = 0; i <sparPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(sparPromos.getResourceId(i,0)));
                }
                break;
            case 4:
                for (int i = 0; i <woolPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(woolPromos.getResourceId(i,0)));
                }
                break;
        }
    }


    private void findViews(View view) {
        pagerPromotions = (ViewPager) view.findViewById(R.id.view_pager_promotions);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Promotions");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        itemSearch= menu.findItem(R.id.menu_search);
        itemHome = menu.findItem(R.id.menu_done);
        itemHome.setTitle("Share");
        itemSearch.setVisible(false);
        itemHome.setVisible(true);
        itemHome.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        itemSearch.setVisible(true);
        itemHome.setVisible(false);
    }

}