package com.parasme.swopinfo.adapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentGroupDetail;
import com.parasme.swopinfo.model.Follow;
import com.parasme.swopinfo.model.Group;
import com.parasme.swopinfo.model.Upload;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.parasme.swopinfo.activity.MainActivity.replaceFragment;

/**
 * Created by SoNu on 6/6/2016.
 */

public class SearchAdapter extends BaseAdapter {

    private int resourceId;
    private ArrayList<Follow> userArrayList;
    private ArrayList<Upload> uploadArrayList;
    private ArrayList<Group> groupArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;
    private int listType;       // 0 - User, 1 - Uploads, 2 - GroupOrBusiness


    public SearchAdapter(Context context, int resourceId, ArrayList<Follow> userArrayList, ArrayList<Upload> uploadArrayList, ArrayList<Group> groupArrayList, int listType) {
        // TODO Auto-generated constructor stub
        this.userArrayList = userArrayList;
        this.uploadArrayList = uploadArrayList;
        this.groupArrayList = groupArrayList;
        this.userArrayList = userArrayList;
        this.listType = listType;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int count=0;
        switch (listType){
            case 0:
                count = userArrayList.size();
                break;
            case 1:
                count = uploadArrayList.size();
                break;
            case 2:
                count = groupArrayList.size();
                break;
        }
        return count;
   }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    class ViewHolder
    {
        LinearLayout layoutUser,layoutGroup,layoutUpload,layoutCompanyDetails;
        ImageView imageUser,imageGroup,imageFileThumb;
        TextView textHeader, textCity,textInfo1,textInfo2,textDescription;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;

        switch (listType){
            case 0:
                view = getUserView(view,parent,position);
                break;
            case 1:
                view = getUploadView(view,parent,position);
                break;
            case 2:
                view = getGroupView(view,parent,position);
                break;
        }


        return view;
    }

    private View getGroupView(View view, ViewGroup parent, final int position) {
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);

            viewHolder.layoutUser = (LinearLayout) view.findViewById(R.id.layoutUsers);
            viewHolder.layoutUpload = (LinearLayout) view.findViewById(R.id.layoutUploads);
            viewHolder.layoutCompanyDetails = (LinearLayout) view.findViewById(R.id.layoutCompanyDetail);
            viewHolder.layoutGroup = (LinearLayout) view.findViewById(R.id.layoutGroup);
            viewHolder.imageGroup = (ImageView) view.findViewById(R.id.imageGroup);
            viewHolder.textHeader = (TextView) view.findViewById(R.id.textHeader);
            viewHolder.textInfo1 = (TextView) view.findViewById(R.id.textExtraInfo1);
            viewHolder.textInfo2 = (TextView) view.findViewById(R.id.textExtraInfo2);
            viewHolder.textCity = (TextView) view.findViewById(R.id.textCity);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.layoutUser.setVisibility(View.GONE);
        viewHolder.layoutUpload.setVisibility(View.GONE);
        viewHolder.layoutGroup.setVisibility(View.VISIBLE);

        final String type = groupArrayList.get(position).getType();

        viewHolder.textHeader.setText(groupArrayList.get(position).getGroupName());

        String groupThumbURL;
        if(type.equals("company")) {
            groupThumbURL = AppConstants.URL_DOMAIN+"upload/company" + groupArrayList.get(position).getGroupId() + "/logo.jpg";
            viewHolder.layoutCompanyDetails.setVisibility(View.VISIBLE);
            viewHolder.textInfo1.setText(groupArrayList.get(position).getExtraInfo1());
            viewHolder.textInfo2.setText(groupArrayList.get(position).getExtraInfo2());
            viewHolder.textCity.setText(groupArrayList.get(position).getBusinessCity());
        }
        else {
            viewHolder.layoutCompanyDetails.setVisibility(View.GONE);
            groupThumbURL = AppConstants.URL_DOMAIN+"upload/group" + groupArrayList.get(position).getGroupId() + "/logo.jpg";
        }
            Picasso.with(context).load(groupThumbURL)
                    .error(R.drawable.companylogo)
                    .placeholder(R.drawable.avtar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(viewHolder.imageGroup);


        viewHolder.layoutGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String type = groupArrayList.get(position).getType();
                if(type.equals("company")){
                    Fragment fragment = new FragmentCompany();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isOwnCompany", false);
                    bundle.putInt(AppConstants.KEY_COMPANY_ID, groupArrayList.get(position).getGroupId());
                    fragment.setArguments(bundle);
                    replaceFragment(fragment,((AppCompatActivity)context).getFragmentManager(),((AppCompatActivity)context),R.id.content_frame);
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.KEY_GROUP_ID,groupArrayList.get(position).getGroupId()+"");
                    bundle.putString(AppConstants.KEY_GROUP_NAME,groupArrayList.get(position).getGroupName());
                    bundle.putString(AppConstants.KEY_USER_ID,"0");
                    Fragment fragment = new FragmentGroupDetail();
                    fragment.setArguments(bundle);
                    replaceFragment(fragment, ((AppCompatActivity)context).getFragmentManager(), ((AppCompatActivity)context), R.id.content_frame);

                }
            }
        });

        return view;
    }

    private View getUploadView(View view, ViewGroup parent, int position) {
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);

            viewHolder.layoutUser = (LinearLayout) view.findViewById(R.id.layoutUsers);
            viewHolder.layoutUpload = (LinearLayout) view.findViewById(R.id.layoutUploads);
            viewHolder.layoutGroup = (LinearLayout) view.findViewById(R.id.layoutGroup);
            viewHolder.textHeader = (TextView) view.findViewById(R.id.textHeader);
            viewHolder.textDescription = (TextView) view.findViewById(R.id.textFileDesc);
            viewHolder.imageFileThumb = (ImageView) view.findViewById(R.id.imageFileThumb);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.layoutUser.setVisibility(View.GONE);
        viewHolder.layoutUpload.setVisibility(View.VISIBLE);
        viewHolder.layoutGroup.setVisibility(View.GONE);

        viewHolder.textHeader.setText(uploadArrayList.get(position).getTitle());
        viewHolder.textDescription.setText(uploadArrayList.get(position).getDescription());

        Picasso.with(context).load(uploadArrayList.get(position).getThumbURL()).placeholder(R.drawable.document_gray).error(R.drawable.document_gray).into(viewHolder.imageFileThumb);
        return view;
    }

    private View getUserView(View view, ViewGroup parent, int position) {

        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);

            viewHolder.layoutUser = (LinearLayout) view.findViewById(R.id.layoutUsers);
            viewHolder.layoutUpload = (LinearLayout) view.findViewById(R.id.layoutUploads);
            viewHolder.layoutGroup = (LinearLayout) view.findViewById(R.id.layoutGroup);
            viewHolder.imageUser = (ImageView) view.findViewById(R.id.imageUser);
            viewHolder.textHeader = (TextView) view.findViewById(R.id.textHeader);
            viewHolder.textCity = (TextView) view.findViewById(R.id.textHeader);


            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.layoutUser.setVisibility(View.VISIBLE);
        viewHolder.layoutUpload.setVisibility(View.GONE);
        viewHolder.layoutGroup.setVisibility(View.GONE);

        String imageURL=AppConstants.URL_DOMAIN+"upload/user"+ userArrayList.get(position).getUserId()+"/profilepic.jpg";
        Picasso.with(context).load(imageURL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(viewHolder.imageUser);
        viewHolder.textHeader.setText(userArrayList.get(position).getFullName());
        viewHolder.textHeader.setText(userArrayList.get(position).getFullName());

        return view;
    }

}