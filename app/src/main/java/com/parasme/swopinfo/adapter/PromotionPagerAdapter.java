package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.model.Store;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SoNu on 3/29/2017.
 */

public class PromotionPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<Store.Promotion> mResources;

    public PromotionPagerAdapter(Context context, ArrayList<Store.Promotion> mResources) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.promotion_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.image_single_promotion);
        Picasso.with(mContext).load(mResources.get(position).getImageURL()).placeholder(R.drawable.app_icon).error(R.drawable.document_gray).into(imageView);
        //imageView.setImageResource(mResources.get(position).image);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}