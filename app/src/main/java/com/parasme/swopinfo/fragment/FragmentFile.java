package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.CommentAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.CustomWebView;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.Progress;
import com.parasme.swopinfo.webservice.ProgressListener;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okio.BufferedSink;
import okio.Okio;

import static com.parasme.swopinfo.helper.Utils.createAudioFileURL;
import static com.parasme.swopinfo.helper.Utils.createFileURL;
import static com.parasme.swopinfo.helper.Utils.createThumbURL;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentFile extends Fragment implements View.OnClickListener{

    private ArrayList<Comment> commentArrayList =new ArrayList<>();
    private String fileId,downloadCount,viewCount, userId;
    public static String fileOwnerId;
    private CommentAdapter adapter;
    private Upload uploadData;
    private String fileURLtoShare;
    private boolean isLiked = false;

    public static TextView textComments;
    private  TextView textViewCount,textDownloadCount,textDownload,textUserFullName,textViewScore;
    private  EditText editComment;
    private  Button btnAddComment;
    private  ListView listComments;
    private  ImageView imageLike;
    private  ImageView imageDisLike, imageMediaPlayPause, imageShare;
    private CustomWebView webView;
    private  ScrollView scrollView;
    private String finalWebViewURL ="",downloadURL;
    private AppCompatActivity mActivity;
    private RelativeLayout layoutMP3View;
    MediaPlayer mediaplayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_file, container, false);

        findViews(view);

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        // Getting file data from bundle and making file url
        fileId = this.getArguments().getInt("fildeId")+"";
        getFileData(fileId);

        // Method calling to get file comments
        getComments(fileId);

        return view;
    }

    private void findViews(View view) {
        textComments = (TextView) view.findViewById(R.id.textComments);
        textViewCount = (TextView) view.findViewById(R.id.textViewCount);
        textViewScore = (TextView) view.findViewById(R.id.textScore);
        textUserFullName = (TextView) view.findViewById(R.id.textUserFullName);
        textDownload = (TextView) view.findViewById(R.id.textDownload);
        textDownloadCount = (TextView) view.findViewById(R.id.textDownloadCount);
        editComment = (EditText) view.findViewById(R.id.editComment);
        btnAddComment = (Button) view.findViewById(R.id.btnAddComment);
        listComments = (ListView) view.findViewById(R.id.listComments);
        imageLike = (ImageView) view.findViewById(R.id.imageLike);
        imageDisLike = (ImageView) view.findViewById(R.id.imageDisLike);
        webView = (CustomWebView) view.findViewById(R.id.webView);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        layoutMP3View = (RelativeLayout) view.findViewById(R.id.mp3View);
        imageMediaPlayPause = (ImageView) view.findViewById(R.id.img_media_play);
        imageShare = (ImageView) view.findViewById(R.id.imgShare);

        imageLike.setOnClickListener(this);
        imageDisLike.setOnClickListener(this);
        btnAddComment.setOnClickListener(this);
        textDownload.setOnClickListener(this);
        imageShare.setOnClickListener(this);
    }

    private void loadWebView() {
        webView.setVisibility(View.VISIBLE);
        layoutMP3View.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(1);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.loadUrl(finalWebViewURL);
        textDownloadCount.setText(downloadCount);
        textViewCount.setText(viewCount);
    }

    private void getFileData(String fileId) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("FILE DAT", response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            prepareFileData(jsonObject);
                        }
                        catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(AppConstants.URL_DOMAIN+"api/file/"+fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void prepareFileData(JSONObject jsonObject) {
        uploadData = new Upload();
        uploadData.setFileId(Integer.parseInt(fileId));
        uploadData.setRealFileName(jsonObject.optString("realfilename"));
        uploadData.setFileName(jsonObject.optString("filename"));
        uploadData.setFileType(jsonObject.optString("filetype"));
        uploadData.setFileSize(jsonObject.optString("filesize"));
        fileOwnerId = jsonObject.optInt("userid")+"";
        uploadData.setTimeDate(jsonObject.optString("timedate"));
        uploadData.setBroadcast(jsonObject.optBoolean("broadcast"));
        uploadData.setComments(jsonObject.optBoolean("comments"));
        uploadData.setRatings(jsonObject.optBoolean("ratings"));
        uploadData.setEmbedding(jsonObject.optBoolean("embedding"));
        uploadData.setDownloads(jsonObject.optBoolean("downloads"));
        uploadData.setTitle(jsonObject.optString("title"));
        uploadData.setDescription(jsonObject.optString("description"));
        uploadData.setViewsCount(jsonObject.optInt("viewscount"));
        uploadData.setDownloadsCount(jsonObject.optInt("downloadscount"));
        uploadData.setCompanyId(jsonObject.optInt("companyid"));
        uploadData.setCompanyUploaded(jsonObject.optBoolean("companyuploaded"));
        uploadData.setUserFullName(jsonObject.optString("userfullname"));
        String thumbName = jsonObject.optString("Thumbnailfilename");
        String videoURL = jsonObject.optString("videourl");
        uploadData.setVideoURL(videoURL);
        String folderName = jsonObject.optString("foldername");
        uploadData.setFolderName(folderName);
        uploadData.setScore(jsonObject.optInt("Score"));
        // Creating Thumb url on basis of either videourl or file
        if(jsonObject.optString("filetype").equalsIgnoreCase("videourl")  && videoURL.contains("youtube"))
        {
            String videoId="";
            if(videoURL.contains("v="))
                videoId = videoURL.split("=")[1];
            else if(videoURL.contains("embed"))
                videoId = videoURL.substring(videoURL.lastIndexOf("/")+1,videoURL.length());
            Log.e("vvvvv", "run: "+videoId );
            uploadData.setThumbURL("http://img.youtube.com/vi/"+videoId+"/default.jpg");
        }
        else
            uploadData.setThumbURL(createThumbURL(jsonObject.optInt("userid")+"",folderName, thumbName, jsonObject.optString("filetype"), jsonObject.optInt("companyid")));

        // Extra check for audio
        if (jsonObject.optString("filetype").contains("audio"))
            uploadData.setFileURL(createAudioFileURL(jsonObject));
        else
            uploadData.setFileURL(createFileURL(jsonObject));

        setFileData();
    }

    private void setFileData() {
        userId = AppConstants.USER_ID;
        //bitmap = ((BitmapDrawable)imageFile.getDrawable()).getBitmap();
        fileOwnerId = uploadData.getOwnerId()+"";

        textUserFullName.setText("Profile Data: "+uploadData.getUserFullName());
        textUserFullName.setOnClickListener(this);
        textViewScore.setText(uploadData.getScore()+"");

        fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+fileId;
        downloadCount = uploadData.getDownloadsCount()+"";
        viewCount = uploadData.getViewsCount()+"";
        String fileType = uploadData.getFileType();
        Log.e("FILETYPE,",fileType);

        if(fileType.equalsIgnoreCase("videourl")){
            finalWebViewURL = uploadData.getVideoURL();
            textDownload.setVisibility(View.GONE);
        }

        else if(fileType.contains("image") || fileType.contains("video") || fileType.contains("audio")){
            finalWebViewURL = uploadData.getFileURL();
        }

        else{
            try {
                String urlEncoded = URLEncoder.encode(uploadData.getFileURL(), "UTF-8");
                finalWebViewURL = "https://docs.google.com/viewer?url=" + urlEncoded;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Log.e("FINAL WURL", finalWebViewURL);

        // Method call for load file in a  webview
        if (fileType.contains("audio"))
            loadMediaPlayer();
        else
            loadWebView();

    }

    private void loadMediaPlayer() {
        textDownloadCount.setText(downloadCount);
        textViewCount.setText(viewCount);

        webView.setVisibility(View.GONE);
        layoutMP3View.setVisibility(View.VISIBLE);
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);

                try {
                    mp.reset();
                    mp.setDataSource(finalWebViewURL);
                    mp.prepare();
                }catch (Exception e){e.printStackTrace();}
                }
        });

        try{
            mediaplayer.setDataSource(finalWebViewURL);
            mediaplayer.prepare();
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        imageMediaPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaplayer.isPlaying()) {
                    imageMediaPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    mediaplayer.start();
                }
                else {
                    imageMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    mediaplayer.pause();
                }
            }
        });
    }

    public void validateAndAddComment(EditText editComment, String fileId, String userId, boolean isOnFileScreen){
        String comment=editComment.getText().toString();
        if(comment.equals("")){
            editComment.setError("Please enter a comment first");
            editComment.requestFocus();
            return;
        }

        editComment.clearFocus();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String timeDate = formatter1.format(date);

        addComment("0",fileId,userId,comment,timeDate,timeDate,editComment,mActivity);
    }


    private void getComments(final String fileId) {
        commentArrayList.clear();
        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("COMMENTS",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonCommentObject = jsonArray.optJSONObject(i);
                                JSONObject jsonUserObject = jsonCommentObject.optJSONObject("User");

                                Comment comment = new Comment();
                                comment.setCommentId(jsonCommentObject.optInt("commentid"));
                                comment.setFileId(jsonCommentObject.optInt("fileid"));
                                comment.setUserId(jsonCommentObject.optInt("userid"));
                                comment.setCommentText(jsonCommentObject.optString("commenttext"));
                                comment.setTimeDate(jsonCommentObject.optString("timedate"));
                                comment.setDate(jsonCommentObject.optString("DisplayDate"));
                                comment.setUserName(jsonUserObject.optString("username"));
                                comment.setUserEmail(jsonUserObject.optString("userEmail"));
                                comment.setUserFirstName(jsonUserObject.optString("userFirstname"));
                                comment.setUserLastName(jsonUserObject.optString("userLastname"));
                                comment.setUserFullName(jsonUserObject.optString("UserFullName"));
                                comment.setReceiveNotification(jsonUserObject.optBoolean("ReceiveEmailNotifications"));
                                comment.setCompanyId(jsonUserObject.optInt("companyid"));
                                comment.setInviteContacts(jsonUserObject.optBoolean("InviteContacts"));
                                comment.setInviteContactTotalHits(jsonUserObject.optInt("InviteContactsTotalHits"));
                                String imageURL=AppConstants.URL_DOMAIN+"upload/user"+ jsonCommentObject.optInt("userid")+"/profilepic.jpg";
                                comment.setUserImageURL(imageURL);
                                commentArrayList.add(comment);
                            }
                            adapter=new CommentAdapter(mActivity,R.layout.row_comment,commentArrayList);
                            listComments.setAdapter(adapter);
                            textComments.setText("Comments ("+ commentArrayList.size()+")");

                            // API call to get votes for a file
                            getVotes(AppConstants.URL_DOMAIN+"api/file/"+fileId+"/getvote/"+AppConstants.USER_ID);

                        }catch (JSONException e){e.printStackTrace();}

                    }
                });
            }
        };
        try {
            webServiceHandler.get(AppConstants.URL_GET_COMMENTS + fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Detail");
        webView.onResume();
    }

    public void likePost(final String fileId, String userId, final ImageView imageLike, final ImageView imageDisLike, final Activity activity) {
        String url=AppConstants.URL_FILE + fileId+"/voteup/"+userId;
        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"test"},new String[]{"test"});

        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("LIKE",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")){
                                getFileData(fileId);
                                Snackbar.make(activity.findViewById(android.R.id.content),"You successfully up voted this post", Snackbar.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(url,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void disLikePost(final String fileId, String userId, final ImageView imageLike, final ImageView imageDisLike, final Activity activity) {
        String url=AppConstants.URL_FILE+fileId+"/votedown/"+userId;

        FormBody.Builder builder=WebServiceHandler.createBuilder(new String[]{"test"},new String[]{"test"});

        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("DISLIKE",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")){
                                getFileData(fileId);
                                Snackbar.make(activity.findViewById(android.R.id.content),"You successfully down voted this post", Snackbar.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(url,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addComment(String commentId, String fileId, String userId, final String commentText, String timeDate, String timeDate1, final EditText editComment, final Activity activity) {
        String [] paramsArray = {"commentid","fileid","userid","commenttext","timedate","date"};
        String [] valuesArray = {commentId,fileId,userId,commentText,timeDate,timeDate1};
        FormBody.Builder builder = WebServiceHandler.createBuilder(paramsArray,valuesArray);

        WebServiceHandler webServiceHandler =  new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("AddComment1",response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")) {
                                editComment.setText("");

                                String userFullName = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME) + " " + SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME);
                                SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM yyyy");
                                Date date = new Date();
                                String strDate = formatter1.format(date);

                                Comment comment = new Comment();
                                comment.setUserId(Integer.parseInt(AppConstants.USER_ID));
                                comment.setUserImageURL(AppConstants.USER_IMAGE_URL);
                                comment.setUserFullName(userFullName);
                                comment.setDate(strDate);
                                comment.setCommentText(commentText);

                                commentArrayList.add(comment);
                                adapter.notifyDataSetChanged();
                                scrollView.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                }, 100);

                                textComments.setText("Comments (" + commentArrayList.size() + ")");
                            }
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };

        try {
            webServiceHandler.post(AppConstants.URL_ADD_COMMENT,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getVotes(final String url){
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Votes", "onResponse: "+response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            if(response.equals(null) || response.equals("null")){
                                imageLike.setClickable(true);
                                imageDisLike.setClickable(true);
                            }
                            else{
                                isLiked = new JSONObject(response).optInt("vote")==1 ? true : false;

                                if(isLiked) {
                                    imageLike.setImageResource(R.drawable.ic_like);
                                    imageDisLike.setImageResource(R.drawable.ic_dislike_normal);
                                    imageLike.setClickable(false);
                                    imageDisLike.setClickable(true);
                                }
                                else{
                                    imageLike.setImageResource(R.drawable.ic_like_normal);
                                    imageDisLike.setImageResource(R.drawable.ic_dislike);
                                    imageLike.setClickable(true);
                                    imageDisLike.setClickable(false);
                                }
                            }

                        }catch (JSONException e){

                        }
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
    public void onPause() {
        super.onPause();
        Log.e("abcd", "onPause: " );
        webView.onPause();
        if (mediaplayer!=null) {
            imageMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);
            try {
                mediaplayer.reset();
                mediaplayer.setDataSource(finalWebViewURL);
                mediaplayer.prepare();
            }catch (Exception e){e.printStackTrace();}
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) activity;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgShare:
                Utils.shareURLCustomIntent(fileURLtoShare, mActivity);
                break;
            case R.id.imageLike:
                imageLike.setImageResource(R.drawable.ic_like);
                imageDisLike.setImageResource(R.drawable.ic_dislike_normal);
                likePost(fileId, userId,imageLike,imageDisLike,mActivity);
                break;
            case R.id.imageDisLike:
                imageLike.setImageResource(R.drawable.ic_like_normal);
                imageDisLike.setImageResource(R.drawable.ic_dislike);
                disLikePost(fileId, userId,imageLike,imageDisLike,mActivity);
                break;
            case R.id.btnAddComment:
                validateAndAddComment(editComment,fileId, userId,true);
                break;
            case R.id.textDownload:
                try {
                    Utils.downloadFile(uploadData.getFileURL(), mActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textUserFullName:
                Fragment fragment=new FragmentUser();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID,fileOwnerId);
                bundle.putString(AppConstants.KEY_USER_NAME,uploadData.getUserFullName());
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment, getFragmentManager(),mActivity,R.id.content_frame);
                break;
        }
    }


    private File ReadFromAssets()
    {
        AssetManager assetManager = mActivity.getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(mActivity.getFilesDir(), "sample.pdf");
        try
        {
            in = assetManager.open("sample.pdf");
            out = mActivity.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        return file;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}