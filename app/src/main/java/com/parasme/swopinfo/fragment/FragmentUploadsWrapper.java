package com.parasme.swopinfo.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FolderAdapter;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Folder;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

import static com.parasme.swopinfo.fragment.FragmentAdd.broadcastArray;
import static com.parasme.swopinfo.fragment.FragmentFolders.folderArrayList;
import static com.parasme.swopinfo.fragment.FragmentUploads.adapter;
import static com.parasme.swopinfo.fragment.FragmentUploads.uploadArrayList;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentUploadsWrapper extends BaseFragment implements FileSelectionActivity.FilePicker, Utils.MoveFileHelper, ImagePicker.Picker, Utils.FolderCreateHelper {

    protected View uploadWrapperView;
    protected LinearLayout layoutUploadWrapper;
    protected Button btnUploads;
    protected Button btnFolders;
    protected ImageView btnImageAdd;
    public static ArrayList<File> fileArrayList=new ArrayList<>();
    private String selectedFolderName="";
    public static GridView gridUploadsMultipleSelection;
    public static TextView textFilesCount;

    // Flags to use once loaded files and folders for current user when re clicking on views
    public boolean isFilesLoaded, isFoldersLoaded;
    private TextView textUploadViews, textUploadDownloads;
    public static TextView emptyGridText;
    private boolean isFileSelectionClicked;

    @Override
    public void onResume() {
        super.onResume();

        LinearLayout layout = (LinearLayout) baseView.findViewById(R.id.layout);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);

        //        if(layoutUploadWrapper == null || layoutUploadWrapper.findViewById(view.getId())==null ) {

        if(!isFileSelectionClicked) {
            uploadWrapperView = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads_wrapper, null);

            imageActionUploads.setImageResource(R.drawable.ic_uploads_active);

            ((TextView) mActivity.findViewById(R.id.text_title)).setText("Uploads");

            layoutUploadWrapper = (LinearLayout) uploadWrapperView.findViewById(R.id.layoutUploadWrapper);

            btnUploads = (Button) uploadWrapperView.findViewById(R.id.btnUploads);
            btnFolders = (Button) uploadWrapperView.findViewById(R.id.btnFolders);
            btnImageAdd = (ImageView) uploadWrapperView.findViewById(R.id.btnImageAdd);
            textUploadViews = (TextView) uploadWrapperView.findViewById(R.id.textUploadViews);
            textUploadDownloads = (TextView) uploadWrapperView.findViewById(R.id.textUploadDownloads);

            textUploadViews.setText("Views: "+AppConstants.UPLOAD_VIEWS);
            textUploadDownloads.setText("Downloads: "+AppConstants.UPLOAD_DOWNLOADS);

            btnUploads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaultViews();
                    btnUploads.setBackgroundResource(R.drawable.btn_bg_active);
                    prepareUploadsView(AppConstants.URL_USER + AppConstants.USER_ID + "/files");
                }
            });
            btnFolders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaultViews();
                    btnFolders.setBackgroundResource(R.drawable.btn_bg_active);
                    prepareFolderViews();
                }
            });
            btnImageAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaultViews();
                    btnImageAdd.setBackgroundResource(R.drawable.btn_bg_active);
                    prepareAddView();
                }
            });


            layout.removeAllViews();
            layout.addView(uploadWrapperView);

            prepareUploadsView(AppConstants.URL_USER + AppConstants.USER_ID + "/files");

            // Calling folder get API to show while moving files between folders
            folderArrayList.clear();
            new FragmentFolders().getFolders(AppConstants.USER_ID, mActivity, null, null);
        }

        else{
            isFileSelectionClicked = false;
        }
    }

    private void prepareUploadsView(String url) {

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Uploads");

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_uploads, null);

        gridUploadsMultipleSelection = (GridView) view.findViewById(R.id.gridUploads);
        emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

        layoutUploadWrapper.removeAllViews();
        //uploadWrapperLayout.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(view);

        adapter = new UploadAdapter(mActivity, R.layout.item_upload, uploadArrayList);
        gridUploadsMultipleSelection.setAdapter(adapter);

        // Method to set multi selection mode to gridview
        Utils.moveFileHelper = FragmentUploadsWrapper.this;
        Utils.folderCreateHelper = FragmentUploadsWrapper.this;
        Utils.setGridMultiSelection(mActivity,gridUploadsMultipleSelection,adapter);


        gridUploadsMultipleSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putInt("fildeId",uploadArrayList.get(i).getFileId());
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
            }
        });

        // Load files only when files are not loaded
        if(!isFilesLoaded) {
            new FragmentUploads().getUploads(url, AppConstants.USER_UPLOADS, gridUploadsMultipleSelection, emptyGridText, mActivity);
            // change flag true so next time api will not load except when returning from folder's files
            if(!url.contains("Files/"))
                isFilesLoaded = true;
        }
        else
        {
            gridUploadsMultipleSelection.setAdapter(adapter);
        }
    }

    private void prepareFolderViews() {

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Folders");

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_folders, null);

        GridView gridFolders = (GridView) view.findViewById(R.id.gridFolders);
        TextView emptyGridText = (TextView) view.findViewById(R.id.emptyGridText);

/*
        FloatingActionButton btnAddFolder = (FloatingActionButton) view.findViewById(R.id.floatingAddFolder);
        btnAddFolder.setVisibility(View.VISIBLE);
        btnAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog folderDialog = Utils.loadFolderDialog(appCompatActivity);
                EditText editFolderName = (EditText) folderDialog.findViewById(R.id.editFolderName);
                Button btnCreateFolder = (Button) folderDialog.findViewById(R.id.btnCreateFolder);
                Button btnCancel = (Button) folderDialog.findViewById(R.id.btnCancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        folderDialog.dismiss();
                    }
                });

                folderDialog.show();
            }
        });
*/

        layoutUploadWrapper.removeAllViews();
        //uploadWrapperLayout.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(view);

        gridFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isFilesLoaded = false;
                prepareUploadsView(AppConstants.URL_USER + AppConstants.USER_ID + "/Files/" + folderArrayList.get(i).getFolderName());
            }
        });

        // Load only when folders are not loaded for current user
        if(!isFoldersLoaded) {
            new FragmentFolders().getFolders(AppConstants.USER_ID, mActivity, gridFolders, emptyGridText);
            isFoldersLoaded=true;
        }
        else
        {
            gridFolders.setAdapter(new FolderAdapter(mActivity,R.layout.item_folder,folderArrayList,false));
        }
    }

    private void prepareAddView() {

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Upload File");

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);

        layoutUploadWrapper.removeAllViews();
        layoutUploadWrapper.setPadding(0,10,0,0);
        //layoutUploadWrapper.setGravity(Gravity.CENTER);
        layoutUploadWrapper.addView(view);

        findFormViews(view);
    }

    private void findFormViews(View view) {
        final LinearLayout layoutSwop = (LinearLayout) view.findViewById(R.id.layoutSwop);
        final EditText editFolderName = (EditText) view.findViewById(R.id.editFolderName);
        final EditText editSwop = (EditText) view.findViewById(R.id.editSwop);
        final EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) view.findViewById(R.id.editDescription);

        final EditText editYoutubeLink = (EditText) view.findViewById(R.id.editYoutubeLink);
        final EditText editTag = (EditText) view.findViewById(R.id.editTag);
        final Spinner spinnerBroadcast = (Spinner) view.findViewById(R.id.spinnerBroadcast);
        final CheckBox checkBoxComments = (CheckBox) view.findViewById(R.id.checkBoxComments);
        final CheckBox checkBoxRatings = (CheckBox) view.findViewById(R.id.checkBoxRatings);
        final CheckBox checkBoxEmbedded = (CheckBox) view.findViewById(R.id.checkBoxEmbedded);
        final CheckBox checkBoxDownloads = (CheckBox) view.findViewById(R.id.checkBoxDownloads);
        final Button btnUpload = (Button) view.findViewById(R.id.btnUpload);
        final Button btnSelectFile = (Button) view.findViewById(R.id.btnSelectFile);
        textFilesCount = (TextView) view.findViewById(R.id.textFilesCount);

        editFolderName.setVisibility(View.VISIBLE);
        layoutSwop.setVisibility(View.GONE);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,broadcastArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroadcast.setAdapter(adapter);

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFileSelectionClicked = true;
                new Utils(mActivity).selectDialog(new String[]{"Camera","Gallery","Files"},FragmentUploadsWrapper.this, FragmentUploadsWrapper.this);
//                FileSelectionActivity.setOnFilePickListener(FragmentUploadsWrapper.this);
//                startActivity(new Intent(appCompatActivity,FileSelectionActivity.class));
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentAdd().uploadClick(editSwop,editFolderName,editTitle,editDescription,editYoutubeLink,editTag,spinnerBroadcast,checkBoxComments,checkBoxRatings,checkBoxEmbedded,checkBoxDownloads,mActivity,AppConstants.USER_ID,"0","0",fileArrayList);
            }
        });
    }

    public void setDefaultViews() {
        btnUploads.setBackgroundResource(R.drawable.btn_bg_inactive);
        btnFolders.setBackgroundResource(R.drawable.btn_bg_inactive);
        btnImageAdd.setBackgroundResource(R.drawable.btn_bg_inactive);
    }

    @Override
    public void onFilesSelected(ArrayList<File> resultFileList) {
        fileArrayList = resultFileList;
        int size = fileArrayList.size();
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText((size==1) ? "1 File Selected" : (size+" Files Selected"));

        Log.e("asfdsaf",resultFileList.get(0).getPath());
/*
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl((Uri.fromFile(resultFileList.get(0))).toString());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        Log.e("TYPE",type);
*/
    }

    @Override
    public void onFileMoveButtonClicked(final Object[] object) {
        final FolderAdapter adapter;
        final Dialog dialogMove =((Dialog)object[0]);
        dialogMove.show();

        final GridView gridFolders = ((GridView)object[2]);
        final TextView textMove = ((TextView) object[1]);
        Button btnCancel = ((Button) object[3]);
        Button btnMove = ((Button)object[4]);

        adapter = new FolderAdapter(mActivity,R.layout.item_folder,folderArrayList,false);
        gridFolders.setAdapter(adapter);
        gridFolders.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        gridFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Grid", "onItemClick: "+folderArrayList.get(i).getFolderName());
                gridFolders.getChildAt(adapter.selectedPosition).setBackgroundResource(0);
                view.setBackgroundResource(R.color.colorPrimary);
                adapter.selectedPosition = i;
                selectedFolderName = folderArrayList.get(i).getFolderName();
                textMove.setText("Moving File(s) to "+ selectedFolderName);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMove.dismiss();
            }
        });

        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMove.dismiss();
                moveFiles((ArrayList<String>) object[5], selectedFolderName);
            }
        });

    }

    private void moveFiles(ArrayList<String> fileIds, final String targetFolderName){
        ArrayList<String> paramsList = new ArrayList<>();
        ArrayList<String> valuesList = new ArrayList<>();

        for (int i = 0; i < fileIds.size(); i++) {
            paramsList.add("fileids");
            valuesList.add(fileIds.get(i));
        }

        paramsList.add("newfolder");
        valuesList.add(targetFolderName);

        String[] paramsArray = new String[paramsList.size()];
        String[] valuesArray = new String[valuesList.size()];

        paramsArray = paramsList.toArray(paramsArray);
        valuesArray = valuesList.toArray(valuesArray);

        FormBody.Builder builder = WebServiceHandler.createBuilder(paramsArray, valuesArray);
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Move Resp", "onResponse: "+response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")) {
//                                setDefaultViews();
//                                btnFolders.setBackgroundResource(R.drawable.btn_bg_active);
//                                prepareFolderViews();
                                folderArrayList.clear();
                                new FragmentFolders().getFolders(AppConstants.USER_ID, mActivity, null, null);
                                MyApplication.alertDialog(mActivity, "File(s) are successfully moved to " + targetFolderName, "Move Files");
                            }
                            else
                                MyApplication.alertDialog(mActivity,"File(s) moving unsuccessful", "Move Files");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_FILE + "movefiles",builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onImagePicked(Bitmap bitmap, String imagePath) {
        fileArrayList.clear();
        File file = new File(imagePath);
        fileArrayList.add(file);
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText("1 File Selected");
    }

    @Override
    public void onVideoPicked(String videoPath) {
        fileArrayList.clear();
        File file = new File(videoPath);
        fileArrayList.add(file);
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText("1 Video Selected");

    }

    @Override
    public void onFolderCreate(Object[] object) {
        final Dialog dialogCreateFolder = (Dialog) object[0];
        final EditText editFolderName = (EditText) object[1];
        final Button btnCancel = (Button) object[2];
        final Button btnCreate = (Button) object[3];

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateFolder.dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folName = editFolderName.getText().toString();
                if(folName.equals("")){
                    editFolderName.setError("Please Enter Folder Name First");
                    editFolderName.requestFocus();
                    return;
                }
                createFolder(dialogCreateFolder, folName);
            }
        });
        dialogCreateFolder.show();
    }

    private void createFolder(Dialog dialogCreateFolder, String folderName) {
        Folder folder = new Folder();
        folder.setFolderName(folderName);
        folder.setNumOfFiles("0");
        folder.setUserId(Integer.parseInt(AppConstants.USER_ID));
        folderArrayList.add(folder);
        dialogCreateFolder.dismiss();
        MyApplication.alertDialog(mActivity,"Folder with name '"+folderName+"' created to move","Folder Create");
    }
}