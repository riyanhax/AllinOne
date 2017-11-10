package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.parasme.swopinfo.helper.Utils.createFileURL;
import static com.parasme.swopinfo.helper.Utils.createThumbURL;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentUploads extends FragmentUploadsWrapper {

    public static GridView gridUploads;
    private TextView emptyGridText;
    public static ArrayList<Upload> uploadArrayList=new ArrayList<>();
    private View childView;
    public static UploadAdapter adapter;

    public void getUploads(String url, final String uploadType, final GridView gridView, final TextView emptyText, final Activity activity) {
        uploadArrayList.clear();
        Log.e("UPLOADS URL",url);
        final Fragment currentFragment = activity.getFragmentManager().findFragmentById(R.id.content_frame);

        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("UploadFiles",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray jsonArray=new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){

                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                JSONObject fileObject;
                                Upload upload = new Upload();

                                if(uploadType.equals(AppConstants.GROUP_UPLOADS)){
                                    fileObject = jsonObject.optJSONObject("file");
                                    upload.setGroupFileLinkId(jsonObject.optInt("groupfilelinkid"));
                                    upload.setGroupId(jsonObject.optInt("groupid"));
                                    upload.setOwnerId(jsonObject.optInt("ownerid"));
                                }
                                else{
                                    fileObject = jsonArray.optJSONObject(i);
                                    upload.setOwnerId(jsonObject.optInt("userid"));
                                }

                                upload.setUploadType(uploadType);
                                upload.setUserFullName(fileObject.optString("userfullname"));
                                upload.setUserId(fileObject.optInt("userid"));
                                upload.setFileId(fileObject.optInt("fileid"));
                                upload.setViewsCount(fileObject.optInt("viewscount"));
                                upload.setDownloadsCount(fileObject.optInt("downloadscount"));
                                upload.setCompanyId(fileObject.optInt("companyid"));
                                upload.setStatusInfoId(fileObject.optInt("statusinformationid"));

                                upload.setRealFileName(fileObject.optString("realfilename"));
                                upload.setFileName(fileObject.optString("filename"));
                                upload.setFileType(fileObject.optString("filetype"));
                                upload.setFileSize(fileObject.optString("filesize"));
                                upload.setTimeDate(fileObject.optString("timedate"));
                                upload.setCategory(fileObject.optString("category"));
                                upload.setTitle(fileObject.optString("title"));
                                upload.setTags(fileObject.optString("tags"));
                                upload.setDescription(fileObject.optString("description"));
                                upload.setVeryPdf(fileObject.optString("verypdf"));

                                String folderName = fileObject.optString("foldername");
                                upload.setFolderName(folderName);
                                String thumbName = fileObject.optString("Thumbnailfilename");

                                String videoURL = fileObject.optString("videourl");
                                if(fileObject.optString("filetype").equalsIgnoreCase("videourl")  && videoURL.contains("youtube"))
                                {
                                    String videoId="";
                                    if(videoURL.contains("v="))
                                        videoId = videoURL.split("=")[1];
                                    else if(videoURL.contains("embed"))
                                        videoId = videoURL.substring(videoURL.lastIndexOf("/")+1,videoURL.length());
                                    Log.e("vvvvv", "run: "+videoId );
                                    upload.setThumbURL("http://img.youtube.com/vi/"+videoId+"/default.jpg");
                                }
                                else
                                    upload.setThumbURL(createThumbURL(fileObject.optInt("userid")+"",folderName, thumbName, fileObject.optString("filetype"), fileObject.optInt("companyid")));

                                upload.setVideoURL(videoURL);
                                upload.setBroadcast(fileObject.optBoolean("broadcast"));
                                upload.setComments(fileObject.optBoolean("comments"));
                                upload.setRatings(fileObject.optBoolean("ratings"));
                                upload.setEmbedding(fileObject.optBoolean("embedding"));
                                upload.setDownloads(fileObject.optBoolean("downloads"));
                                upload.setProfileRemoved(fileObject.optBoolean("profileremoved"));
                                upload.setCompanyUploaded(fileObject.optBoolean("companyuploaded"));
                                upload.setIncludeExtension(fileObject.optBoolean("IncludesExtension"));


                                upload.setFileURL(createFileURL(fileObject));

                                if(currentFragment instanceof FragmentUploadsWrapper || currentFragment instanceof FragmentUser){
                                    if(upload.getCompanyId()==0){
                                        if(!upload.getFileType().equals("bookmark"))
                                            uploadArrayList.add(upload);
                                    }
                                }
                                else
                                    uploadArrayList.add(upload);
                            }

                            if(adapter!=null)
                                adapter.notifyDataSetChanged();
                            gridView.setEmptyView(emptyText);
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


    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("My Uploads");

        btnUploads.setBackgroundResource(R.drawable.btn_bg_active);

//        if (childView == null) {
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);

        gridUploads = (GridView) childView.findViewById(R.id.gridUploads);
        emptyGridText = (TextView) childView.findViewById(R.id.emptyGridText);

        layoutUploadWrapper.removeAllViews();
        //layoutUploadWrapper.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(childView);



        String uploadsURL = AppConstants.URL_USER + AppConstants.USER_ID + "/files";

        adapter = new UploadAdapter(mActivity, R.layout.item_upload, uploadArrayList);
        gridUploads.setAdapter(adapter);



        getUploads(uploadsURL, AppConstants.OWN_UPLOADS, gridUploads, emptyGridText, mActivity);

        gridUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("fileData",uploadArrayList.get(i));
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
            }
        });
//        }
    }

    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            int selectCount = gridUploads.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }

    }

}