package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentGroupDetail;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.model.Follow;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SoNu on 6/6/2016.
 */

public class FollowerAdapter extends ArrayAdapter<Follow> {

    private int resourceId;
    private ArrayList<Follow> followArrayList;
    private Context context;
    private ViewHolder viewHolder;
    LayoutInflater vi;
    private boolean isCompanyMember=false;

    public FollowerAdapter(Context context, int resourceId, ArrayList<Follow> followArrayList, boolean isCompanyMember) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.followArrayList = followArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isCompanyMember = isCompanyMember;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        Log.e("SIZE", followArrayList.size()+"");
        return followArrayList.size();
    }


    class ViewHolder
    {
        ImageView imageFollowingUser,imageDelete;
        TextView textUserName;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.imageFollowingUser = (ImageView) view.findViewById(R.id.image_follower);
            viewHolder.textUserName = (TextView) view.findViewById(R.id.textFollowerName);
            viewHolder.imageDelete = (ImageView) view.findViewById(R.id.image_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(isCompanyMember)
            viewHolder.imageDelete.setVisibility(View.GONE);

        viewHolder.textUserName.setText(followArrayList.get(position).getFullName());


        String url;
        if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentGroupDetail){
            url=AppConstants.URL_DOMAIN+"upload/user"+ followArrayList.get(position).getFollowingId()+"/profilepic.jpg";
            if(followArrayList.get(position).getType()==2)
                url = AppConstants.URL_DOMAIN+"upload/company"+followArrayList.get(position).getFollowingId()+"/logo.jpg";
        }
        else {
            url = AppConstants.URL_DOMAIN+"upload/user" + followArrayList.get(position).getUserId() + "/profilepic.jpg";
                if (followArrayList.get(position).getType() == 2)
                    url = AppConstants.URL_DOMAIN+"upload/company" + followArrayList.get(position).getUserId() + "/logo.jpg";

            // If viewing company followers then type returns 2 so to handle this
            if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentCompany)
                url = AppConstants.URL_DOMAIN+"upload/user" + followArrayList.get(position).getUserId() + "/profilepic.jpg";
        }

        Log.e("URL", "getView: "+url );

        Picasso.with(context).load(url)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(viewHolder.imageFollowingUser);

        viewHolder.imageFollowingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new FragmentUser();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID,followArrayList.get(position).getUserId()+"");
                bundle.putString(AppConstants.KEY_USER_NAME,followArrayList.get(position).getFullName());
                fragment.setArguments(bundle);

                if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUser)
                {
                    Intent i = new Intent((Activity) context,MainActivity.class);
                    i.putExtra("startUserWrapper",true);
                    i.putExtra(AppConstants.KEY_USER_ID,followArrayList.get(position).getUserId()+"");
                    i.putExtra(AppConstants.KEY_USER_NAME,followArrayList.get(position).getFullName());
                    ((Activity) context).startActivity(i);
                    ((Activity) context).finish();
                }
                else
                    MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);

            }
        });
        return view;
    }

}