package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.fragment.FragmentAllFiles;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class MultipleFilesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView countryPhoto;
    public RelativeLayout layoutMoreFiles;
    public TextView numOfRemainingFiles;
    public Context context;
    public ArrayList<Upload> uploadList=new ArrayList<>();

    public MultipleFilesViewHolders(View itemView, Context context, List<Upload> uploadArrayList) {
        super(itemView);
        itemView.setOnClickListener(this);
        countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
        layoutMoreFiles = (RelativeLayout) itemView.findViewById(R.id.layout_more_files);
        numOfRemainingFiles = (TextView) itemView.findViewById(R.id.text_num_remaining_files);
        this.context = context;
        uploadList.addAll(uploadArrayList);
    }

    @Override
    public void onClick(View view) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("uploadList",uploadList);
        Fragment fragment=new FragmentAllFiles();
        fragment.setArguments(bundle);
        MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),((Activity) context),R.id.content_frame);

    }
}
