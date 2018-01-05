package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by SoNu on 6/6/2016.
 */

public class SelectCategoryAdapter extends ArrayAdapter<Category> {

    private int resourceId;
    public ArrayList<Category> categoryArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;

    public SelectCategoryAdapter(Context context, int resourceId, ArrayList<Category> categoryArrayList) {
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

    public void removeCategories(String ids) {
        for (int i = getCount()-1; i >=0 ; i--) {
            if(categoryArrayList.get(i).isCategoryChecked()){
                Log.e("MMM",categoryArrayList.get(i).getCategoryName());
                remove(getItem(i));
            }
        }
        notifyDataSetChanged();
/*
        List<String> selectedSkillIds = new LinkedList<String>(Arrays.asList(ids.split(",")));
        for (int i = 0; i < categoryArrayList.size(); i++) {
            if(selectedSkillIds.contains(categoryArrayList.get(i).getCategoryId()+"")){
                Log.e("cat",categoryArrayList.get(i).getCategoryName());
                remove(getItem(i));
            }
        }
        notifyDataSetChanged();
*/
    }


    class ViewHolder
    {
        TextView textCategoryName;
        CheckBox checkBoxCategory;
        ImageView imageCategoryIcon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textCategoryName = (TextView) view.findViewById(R.id.text_category_name);
            viewHolder.imageCategoryIcon = (ImageView) view.findViewById(R.id.image_category_icon);
            viewHolder.checkBoxCategory = (CheckBox) view.findViewById(R.id.checkbox_category);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textCategoryName.setText(categoryArrayList.get(position).getCategoryName());
        viewHolder.checkBoxCategory.setChecked(categoryArrayList.get(position).isCategoryChecked());
        viewHolder.imageCategoryIcon.setImageResource(categoryArrayList.get(position).getCategoryIcon());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("BOX",categoryArrayList.get(position).getCategoryName()+"__"+!categoryArrayList.get(position).isCategoryChecked());
                categoryArrayList.get(position).setCategoryChecked(!categoryArrayList.get(position).isCategoryChecked());
                notifyDataSetChanged();
            }
        });

        viewHolder.checkBoxCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("BOX",categoryArrayList.get(position).getCategoryName()+"__"+!categoryArrayList.get(position).isCategoryChecked());
                categoryArrayList.get(position).setCategoryChecked(!categoryArrayList.get(position).isCategoryChecked());
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public String checkedCategoryIds(){
        String ids = "";
        for (int i = 0; i < categoryArrayList.size(); i++) {
            if(categoryArrayList.get(i).isCategoryChecked())
                ids = ids + categoryArrayList.get(i).getCategoryId()+",";
        }
        if(!ids.equals(""))
            ids = ids.substring(0,ids.length()-1);
        Log.e("ids",ids);
        return ids;
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public void remove(@Nullable Category object) {
        categoryArrayList.remove(object);
    }
}