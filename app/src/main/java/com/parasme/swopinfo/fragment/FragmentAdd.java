package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hendraanggrian.widget.SocialAutoCompleteTextView;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.webservice.Progress;
import com.parasme.swopinfo.webservice.ProgressListener;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentAdd extends FragmentUploadsWrapper implements FileSelectionActivity.FilePicker{

    public static String [] broadcastArray={"Public","Private"};
    private ArrayList<File> fileArrayList=new ArrayList<>();
    private View childView;
    private EditText editFolderName,editSwopText;
    private EditText editTitle;
    private SocialAutoCompleteTextView editDescription;
    private EditText editYoutubeLink;
    private EditText editTag;
    private Spinner  spinnerBroadcast;
    private CheckBox checkBoxComments;
    private CheckBox checkBoxRatings;
    private CheckBox checkBoxEmbedded;
    private CheckBox checkBoxDownloads;
    private Button btnUpload;
    private Button btnSelectFile;


    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Detail");
        btnImageAdd.setBackgroundResource(R.drawable.btn_bg_inactive);

//        if(childView == null) {
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);

        findViews(childView);
        layoutUploadWrapper.removeAllViews();
        layoutUploadWrapper.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(childView);
        //       }
    }

    private void findViews(View childView) {
        final LinearLayout layoutSwop = (LinearLayout) childView.findViewById(R.id.layoutSwop);
        editSwopText = (EditText) childView.findViewById(R.id.editSwop);
        editFolderName = (EditText) childView.findViewById(R.id.editFolderName);
        editTitle = (EditText) childView.findViewById(R.id.editTitle);
        editDescription = (SocialAutoCompleteTextView) childView.findViewById(R.id.editDescription);
        editDescription.setHashtagColor(getResources().getColor(R.color.colorPrimary));
        editDescription.setMentionColor(getResources().getColor(R.color.colorPrimary));
        editDescription.setThreshold(1);

        editYoutubeLink = (EditText) childView.findViewById(R.id.editYoutubeLink);
        editTag = (EditText) childView.findViewById(R.id.editTag);
        spinnerBroadcast = (Spinner) childView.findViewById(R.id.spinnerBroadcast);
        checkBoxComments = (CheckBox) childView.findViewById(R.id.checkBoxComments);
        checkBoxRatings = (CheckBox) childView.findViewById(R.id.checkBoxRatings);
        checkBoxEmbedded = (CheckBox) childView.findViewById(R.id.checkBoxEmbedded);
        checkBoxDownloads = (CheckBox) childView.findViewById(R.id.checkBoxDownloads);
        btnUpload = (Button) childView.findViewById(R.id.btnUpload);
        btnSelectFile = (Button) childView.findViewById(R.id.btnSelectFile);

        editFolderName.setVisibility(View.VISIBLE);
        layoutSwop.setVisibility(View.GONE);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,broadcastArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroadcast.setAdapter(adapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadClick(editSwopText,editFolderName,editTitle,editDescription,editYoutubeLink,editTag,spinnerBroadcast,checkBoxComments,checkBoxRatings,checkBoxEmbedded,checkBoxDownloads,mActivity,AppConstants.USER_ID,"0","0",fileArrayList);
            }
        });
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSelectionActivity.setOnFilePickListener(FragmentAdd.this);
                startActivity(new Intent(mActivity,FileSelectionActivity.class));
            }
        });
    }


    @Override
    public void onFilesSelected(ArrayList<File> resultFileList) {
        fileArrayList = resultFileList;

        Log.e("asfdsaf",resultFileList.get(0).getPath());
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl((Uri.fromFile(resultFileList.get(0))).toString());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        Log.e("TYPE",type);

    }

    public void uploadClick(EditText editSwopText, EditText editFolderName,EditText editTitle,EditText editDescription,EditText editYoutubeLink,EditText editTag,
                            Spinner spinnerBroadcast, CheckBox checkBoxComments, CheckBox checkBoxRatings, CheckBox checkBoxEmbedded, CheckBox checkBoxDownloads,Activity activity,String userId, String companyId, String groupId, ArrayList<File> fileArrayList){

        for (int i = 0; i < fileArrayList.size(); i++) {
            String extension = MimeTypeMap.getFileExtensionFromUrl((Uri.fromFile(fileArrayList.get(i))).toString());
            String type="";
            Log.e("1111111",extension);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Log.e("2222222",type);
                if (type.contains("image"))
                    fileArrayList.set(i, new File(Utils.fixExif(fileArrayList.get(i).getPath())));
            }

        }

        String swopText=editSwopText.getText().toString();
        String folderName=editFolderName.getText().toString();
        String title=editTitle.getText().toString();
        String description=editDescription.getText().toString();
        String videoURL=editYoutubeLink.getText().toString();
        String tags=editTag.getText().toString();
        String broadcastType=spinnerBroadcast.getSelectedItem().toString();
        String broadcast=broadcastType.equalsIgnoreCase("public") ? "true" : "false";
        String allowComments=checkBoxComments.isChecked() ? "true" : "false";
        String allowRatings=checkBoxRatings.isChecked() ? "true" : "false";
        String allowEmbedding=checkBoxEmbedded.isChecked() ? "true" : "false";
        String allowDownloads=checkBoxDownloads.isChecked() ? "true" : "false";

        if(swopText.equals("")) {
            if (title.equals("")) {
                editTitle.setError("Please enter title first");
                editTitle.requestFocus();
                return;
            }

            if (description.equals("")) {
                editDescription.setError("Please enter description first");
                editDescription.requestFocus();
                return;
            }

            if (tags.equals("")) {
                editTag.setError("Please enter tag for upload");
                editTag.requestFocus();
                return;
            }

            if (fileArrayList.size() == 0 && videoURL.equals("")) {
                editYoutubeLink.setError("You did not select file, please enter video URL");
                editYoutubeLink.requestFocus();
                return;
            }
        }

        uploadFile(userId,companyId,groupId,folderName,title,tags,description,broadcast,allowComments,allowRatings,allowEmbedding,allowDownloads,videoURL,fileArrayList,activity,btnUpload,swopText,false);
    }

    public void uploadFile(String userId, String companyId, String groupId, String folderName, String title, String tags, String description, String broadcast, String allowComments, String allowRatings, String allowEmbedding, String allowDownloads, String videoURL, ArrayList<File> fileArrayList, final Activity activity, final Button btnUpload, final String swopText, final boolean isFromShare) {

        String paramsArray[]={"UseriD","newsfeedcomment","companyid","groupid","Folder","Title","Tags","Description","AllowComments","AllowDownloads","AllowEmbedding","AllowRatings","Broadcast","VideoURL"};
        String valuesArray[]={userId,swopText,companyId,groupId,folderName,title,tags,description,allowComments,allowDownloads,allowEmbedding,allowRatings,broadcast,videoURL};

        List<String> paramseList = new ArrayList<String>(Arrays.asList(paramsArray));
        List<String> valuesList = new ArrayList<String>(Arrays.asList(valuesArray));

        for(int i=0;i<fileArrayList.size();i++){
            Log.e("pos"+i, fileArrayList.get(i).getPath());
            paramseList.add("image"+(i+1));
            valuesList.add(fileArrayList.get(i).getPath());
        }

        String[] finalParams = paramseList.toArray(new String[paramseList.size()]);
        String[] finalValues = valuesList.toArray(new String[valuesList.size()]);

        MultipartBody.Builder builder= WebServiceHandler.createMultiPartBuilder(finalParams,finalValues);

        WebServiceHandler webServiceHandler=new WebServiceHandler(activity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("UPLOADRESP",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            FragmentUploadsWrapper.fileArrayList.clear();
                            JSONObject jsonObject=new JSONObject(response);
                            if(1==1)
                            //if(jsonObject.optString("Code").equals("200"))
                            {

                                if(!isFromShare){
                                    Snackbar.make(activity.findViewById(android.R.id.content),"Swopped successfully", Snackbar.LENGTH_LONG).show();
                                    if(FragmentHome.dialogSwop!=null && FragmentHome.dialogSwop.isShowing()){
                                        FragmentHome.dialogSwop.dismiss();
                                        FragmentHome.feedArrayList = new ArrayList<>();
//                                        activity.startActivity(new Intent(activity, MainActivity_.class));
//                                        activity.finish();
                                    }
                                    FragmentUploadsWrapper.fileArrayList.clear();
                                    //new FragmentHome().getFeeds(activity, FragmentHome.listFeeds,FragmentHome.swipeRefreshLayout);


                                    if(FragmentUploads.adapter == null)
                                        FragmentUploads.adapter = new UploadAdapter(activity, R.layout.item_upload, FragmentUploads.uploadArrayList);

                                    if(gridUploadsMultipleSelection==null){
                                        View view = activity.getLayoutInflater().inflate(R.layout.fragment_uploads, null);
                                        gridUploadsMultipleSelection = (GridView) view.findViewById(R.id.gridUploads);
                                    }

                                    new FragmentUploads().getUploads(AppConstants.URL_USER + AppConstants.USER_ID + "/files", AppConstants.USER_UPLOADS, gridUploadsMultipleSelection, emptyGridText, activity);

                                    //btnUpload.setClickable(false);
                                }

                                else{
                                    activity.startActivity(new Intent(activity,MainActivity.class));
                                    if(android.os.Build.VERSION.SDK_INT >= 21)
                                    {
                                        activity.finish();
                                    }
                                    else
                                    {
                                        activity.finish();
                                    }
                                }
                            }
                            else
                                Snackbar.make(activity.findViewById(android.R.id.content), "Swop failed", Snackbar.LENGTH_LONG).show();
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.postMultiPart(AppConstants.URL_UPLOAD_FILE,builder,userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        layoutUploadWrapper.removeAllViews();
    }

}