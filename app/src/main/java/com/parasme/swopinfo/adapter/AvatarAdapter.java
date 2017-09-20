package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentUploadsWrapper;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.parasme.swopinfo.fragment.FragmentCompany.companyId;

/**
 * Created by SoNu on 6/6/2016.
 */

public class AvatarAdapter extends ArrayAdapter<File> {

    private int resourceId;
    private ArrayList<File> fileArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;
    public int selectedPosition = 0;

    public AvatarAdapter(Context context, int resourceId, ArrayList<File> fileArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.fileArrayList = fileArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("SIZE",fileArrayList.size()+"");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fileArrayList.size();
    }

    public void clearSelections() {
        notifyDataSetChanged();
    }


    class ViewHolder
    {
        ImageView imageAvatar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.imageAvatar = (ImageView) view.findViewById(R.id.imageAvatar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        if(fileArrayList.get(position).exists()) {

            Log.e("path",fileArrayList.get(position).getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(fileArrayList.get(position).getAbsolutePath());

            viewHolder.imageAvatar.setImageBitmap(myBitmap);
        }
        else{
            Log.e("check",fileArrayList.get(position).getAbsolutePath());
        }

        if (position == selectedPosition) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorHighlightAvatar));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}