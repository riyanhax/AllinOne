package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FolderAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.model.Folder;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentFolders extends FragmentUploadsWrapper {


    public static ArrayList<Folder> folderArrayList=new ArrayList<>();
    private View childView;
    private GridView gridFolders;
    private TextView emptyGridText;

/*
    void folderClick(int position){
        Log.e("DFKJFDSLF",AppConstants.FOLDER_UPLOADS);
        Bundle bundle =  new Bundle();
        bundle.putString("uploadType" , AppConstants.FOLDER_UPLOADS);
        bundle.putString("folderName" , folderArrayList.get(position).getFolderName());
        Fragment fragment = new FragmentUploads_();
        fragment.setArguments(bundle);

        if(getParentFragment().getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUploadWrapper_){
            getFragmentManager().beginTransaction().replace(R.id.frame_uploads,fragment).addToBackStack(null).commit();
            // MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.frame_uploads);
        }
        else if(getParentFragment().getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentInnerUploadsWrapper_){
            Log.e("AAAA", "folderClick: ");
            getFragmentManager().beginTransaction().replace(R.id.frame_inner_uploads,fragment).addToBackStack(null).commit();
        }
        else if(getParentFragment().getParentFragment().getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUserWrapper_)
            getFragmentManager().beginTransaction().replace(R.id.frame_inner_uploads,fragment).addToBackStack(null).commit();
        else
            Log.e("else", "nl ");

            // MainActivity.replaceFragment(fragment,getChildFragmentManager(),mActivity,R.id.frame_inner_uploads);

    }
*/


    public void getFolders(final String userId, final Activity activity, final GridView gridView, final TextView emptyText) {
        if(folderArrayList.size()==0){
            String url=AppConstants.URL_USER + userId + "/folders/" + userId;
            WebServiceHandler webServiceHandler=new WebServiceHandler(activity);
            webServiceHandler.serviceListener=new WebServiceListener() {
                @Override
                public void onResponse(final String response) {
                    Log.e("FODERS",response);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONArray jsonArray=new JSONArray(response);
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.optJSONObject(i);

                                    Folder folder=new Folder();
                                    folder.setFolderName(jsonObject.optString("DirectoryName"));
                                    folder.setNumOfFiles(jsonObject.optInt("TotalFiles")+"");
                                    folder.setUserId(Integer.parseInt(userId));

                                    folderArrayList.add(folder);
                                }

                                if(gridView!=null) {
                                    gridView.setAdapter(new FolderAdapter(activity, R.layout.item_folder, folderArrayList, false));
                                    gridView.setEmptyView(emptyText);
                                }
                            }catch (JSONException e){e.printStackTrace();}
                        }
                    });
                }
            };
            try {
                webServiceHandler.get(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            gridView.setAdapter(new FolderAdapter(activity,R.layout.item_folder,folderArrayList,false));
            gridView.setEmptyView(emptyText);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Folders");
        btnFolders.setBackgroundResource(R.drawable.btn_bg_active);

//        if (childView == null) {
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_folders, null);

        gridFolders = (GridView) childView.findViewById(R.id.gridFolders);
        emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);

        layoutUploadWrapper.removeAllViews();
        //layoutUploadWrapper.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(childView);

        gridFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prepareUploadsView(folderArrayList.get(i).getFolderName());
            }
        });

        folderArrayList.clear();
        getFolders(AppConstants.USER_ID,mActivity,gridFolders,emptyGridText);
//        }

    }

    private void prepareUploadsView(String folderName){
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);

        GridView gridUploads = (GridView) view.findViewById(R.id.gridUploads);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        layoutUploadWrapper.removeAllViews();
        layoutUploadWrapper.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(view);

        String url = AppConstants.URL_USER + AppConstants.USER_ID + "/Files/" + folderName;
        new FragmentUploads().getUploads(url,AppConstants.USER_UPLOADS,gridUploads,emptyGridText,mActivity);

        gridUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("fileData",FragmentUploads.uploadArrayList.get(i));
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        layoutUploadWrapper.removeAllViews();
    }

}