package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentFavourites;
import com.parasme.swopinfo.fragment.FragmentHome;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SoNu on 6/6/2016.
 */

public class FavouriteAdapter extends ArrayAdapter<Category> {

    private int resourceId;
    private ArrayList<Category> categoryArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;

    public FavouriteAdapter(Context context, int resourceId, ArrayList<Category> categoryArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return categoryArrayList.size();
    }


    class ViewHolder
    {
        TextView textCategoryName;
        ImageView imageRemove, imageCategoryIcon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textCategoryName = (TextView) view.findViewById(R.id.text_favourite);
            viewHolder.imageRemove = (ImageView) view.findViewById(R.id.img_remove);
            viewHolder.imageCategoryIcon = (ImageView) view.findViewById(R.id.image_category_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textCategoryName.setText(categoryArrayList.get(position).getCategoryName());
        viewHolder.imageCategoryIcon.setImageResource(categoryArrayList.get(position).getCategoryIcon());
        viewHolder.imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryArrayList.size()!=1){
                    FragmentHome.retailerList.clear();
                    removeFav(categoryArrayList.get(position).getCategoryId()+"", position);
                }
            }
        });

        return view;
    }

    private void removeFav(String catId, final int position){
        String ids="";
        categoryArrayList.remove(position);
        FragmentFavourites.savedIdsList.remove(catId);
        Log.e("CH",FragmentFavourites.savedIdsList.toString());
        for (int i = 0; i < FragmentFavourites.savedIdsList.size(); i++) {
            ids = ids + FragmentFavourites.savedIdsList.get(i) + ",";
        }
        ids = ids.substring(0,ids.length()-1);
        Log.e("new ids",ids);
        SharedPreferenceUtility.getInstance().save(AppConstants.PREF_FAV_IDS,ids);
        notifyDataSetChanged();
    }
/*
    private void removeFav(String catId, final int position) {
        final Activity activity = (Activity) context;
        String url = "http://dev.swopinfo.com/AddremoveFav.aspx?user_id="+ AppConstants.USER_ID+"&category_ids="+catId+"&action=delete";
        WebServiceHandler webServiceHandler = new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Remove",response);
                categoryArrayList.remove(position);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {

        }
    }
*/


}
