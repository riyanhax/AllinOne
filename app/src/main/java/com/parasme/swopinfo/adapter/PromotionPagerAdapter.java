package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        imageView.setTag(position+"");

        Picasso.with(mContext).load(mResources.get(position).getImageURL()).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).into(imageView);
//        Picasso.with(mContext).load("https://swopinfo.com/upload/company100313/KwiksparRiverbend%20Specials%204th%20-17th%20Dec%2017/294f4f28-0b35-4a04-b257-e2a987067629.jpeg").placeholder(R.drawable.app_icon).error(R.drawable.app_icon).into(imageView);


/*
        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                Toast.makeText(mContext, exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        builder.build().load(mResources.get(position).getImageURL()).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).into(imageView);
*/

        //imageView.setImageResource(mResources.get(position).image);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}