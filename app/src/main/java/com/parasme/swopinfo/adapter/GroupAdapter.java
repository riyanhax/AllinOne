package com.parasme.swopinfo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.model.Group;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SoNu on 6/6/2016.
 */

public class GroupAdapter extends ArrayAdapter<Group> {

    private int resourceId;
    private ArrayList<Group> groupArrayList;
    private Context context;
    private ViewHolder viewHolder;
    LayoutInflater vi;

    public GroupAdapter(Context context, int resourceId, ArrayList<Group> groupArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.groupArrayList = groupArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        Log.e("SIZE", groupArrayList.size()+"");
        return groupArrayList.size();
    }


    class ViewHolder
    {
        ImageView imageGroup;
        TextView textGroupName,textOwnerName,textMemberCount,textFileCount;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);

            viewHolder.imageGroup = (ImageView) view.findViewById(R.id.image_group);
            viewHolder.textGroupName = (TextView) view.findViewById(R.id.textGroupName);
            viewHolder.textOwnerName = (TextView) view.findViewById(R.id.textOwnerName);
            viewHolder.textMemberCount = (TextView) view.findViewById(R.id.textMemberCount);
            viewHolder.textFileCount = (TextView) view.findViewById(R.id.textFileCount);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textGroupName.setText(groupArrayList.get(position).getGroupName());
        viewHolder.textOwnerName.setText("Owner: "+groupArrayList.get(position).getOwnerFullName());
        viewHolder.textMemberCount.setText(""+groupArrayList.get(position).getTotalMembers());
        viewHolder.textFileCount.setText(""+groupArrayList.get(position).getTotalFiles());

        String url= AppConstants.URL_DOMAIN+"upload/group"+groupArrayList.get(position).getGroupId()+"/logo.jpg";
        Picasso.with(getContext()).load(url)
                .error(R.drawable.app_icon)
                .placeholder(R.drawable.app_icon)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(viewHolder.imageGroup);


        return view;
    }
}