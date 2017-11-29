package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.fragment.FragmentHome;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.NonScrollRecyclerView;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.urlpreview.LinkPreviewCallback;
import com.parasme.swopinfo.urlpreview.SourceContent;
import com.parasme.swopinfo.urlpreview.TextCrawler;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.kaelaela.opengraphview.OnLoadListener;
import me.kaelaela.opengraphview.OpenGraphView;
import me.kaelaela.opengraphview.network.model.OGData;
import okhttp3.FormBody;

import static com.parasme.swopinfo.activity.MainActivity.replaceFragment;

/**
 * Created by SoNu on 6/6/2016.
 */

public class FeedAdapter extends ArrayAdapter<Feed> implements View.OnClickListener {

    DisplayImageOptions optionsUserImage = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.avtar)
            .showImageForEmptyUri(R.drawable.avtar)
            .showImageOnFail(R.drawable.avtar)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    DisplayImageOptions optionsFile = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.document_gray)
            .showImageOnFail(R.drawable.document_gray)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

//    private String randomNumber = randomNumber();
    private Map<String, Bitmap> mBitmapCache = new HashMap<String, Bitmap>();
    private int resourceId;
    private static ArrayList<Feed> feedArrayList;
    public static Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;
    private String fileURLtoShare;
    private View view;
    private EditText editComment;
    private Button btnAddComment,btnCancel;
    public static Dialog dialog;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentArrayList;
    public static ArrayList<String> arrayList = new ArrayList<>();
    public ListView listFeeds;

    public FeedAdapter(Context context, int resourceId, ArrayList<Feed> feedArrayList, ListView listFeeds) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.feedArrayList = feedArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrayList.clear();
        this.listFeeds = listFeeds;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return feedArrayList.size();
    }



    public class ViewHolder
    {
        TextView textUserName,textFolderName,textView,textComment,textShare,textUserNameFile,textViewFile,textTime,textTimeFile,
                textMultipleFiles,textPreviewTitle, textPreviewDescription,textPreviewURL;
        ImageView imageUser,imageFileThumb,imageLike,imageDislkike,imageUserFile,imageShareThumb,imagePreviewThumb;
        LinearLayout layoutFileComment,layoutUser,layoutShare,layoutAction,layoutAdditionalInfo,layoutMultipleFiles;
        FloatingActionButton btnLinkedIn,btnMail,btnFacebook,btnTwitter,btnGPlus,btnPinterest,btnWhatsapp;
        FloatingActionsMenu menuShare;
        Button btnComment,btnShareMe;
        NonScrollRecyclerView recyclerView;

        ProgressBar progressBar;
        // Preview
        LinearLayout layoutPreview,infoWrap,layoutPreviewContent;
        View loading;;
        OpenGraphView openGraphView;
        TextCrawler textCrawler;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textUserName = (TextView) view.findViewById(R.id.textUserName);
            viewHolder.textUserNameFile = (TextView) view.findViewById(R.id.textUserNameFile);
            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            viewHolder.textViewFile = (TextView) view.findViewById(R.id.textViewFile);
            viewHolder.textComment = (TextView) view.findViewById(R.id.textComment);
            viewHolder.textShare = (TextView) view.findViewById(R.id.textShared);
            viewHolder.textTime = (TextView) view.findViewById(R.id.textTime);
            viewHolder.textTimeFile = (TextView) view.findViewById(R.id.textTimeFile);
            viewHolder.textMultipleFiles = (TextView) view.findViewById(R.id.textMultipleFiles);
            viewHolder.imageUser = (ImageView) view.findViewById(R.id.imageUser);
            viewHolder.imageUserFile = (ImageView) view.findViewById(R.id.imageUserFile);
            viewHolder.imageFileThumb = (ImageView) view.findViewById(R.id.imageFileThumb);
            viewHolder.imageShareThumb = (ImageView) view.findViewById(R.id.imageShareThumb);
            viewHolder.layoutFileComment = (LinearLayout) view.findViewById(R.id.layoutFileComment);
            viewHolder.layoutPreview = (LinearLayout) view.findViewById(R.id.layoutPreview);
            viewHolder.recyclerView = (NonScrollRecyclerView) view.findViewById(R.id.recycler_view_multiple_files);
            viewHolder.layoutAdditionalInfo = (LinearLayout) view.findViewById(R.id.layoutAdditionalInfo);
            viewHolder.layoutMultipleFiles = (LinearLayout) view.findViewById(R.id.layoutMultipleFiles);
            viewHolder.layoutShare = (LinearLayout) view.findViewById(R.id.layoutShare);
            viewHolder.layoutUser = (LinearLayout) view.findViewById(R.id.layoutUser);
            viewHolder.layoutAction = (LinearLayout) view.findViewById(R.id.layoutActions);
            viewHolder.btnLinkedIn = (FloatingActionButton) view.findViewById(R.id.btnLinkedIn);
            viewHolder.btnMail = (FloatingActionButton) view.findViewById(R.id.btnMail);
            viewHolder.btnFacebook = (FloatingActionButton) view.findViewById(R.id.btnFacebook);
            viewHolder.btnTwitter = (FloatingActionButton) view.findViewById(R.id.btnTwitter);
            viewHolder.btnGPlus = (FloatingActionButton) view.findViewById(R.id.btnGPlus);
            viewHolder.btnPinterest = (FloatingActionButton) view.findViewById(R.id.btnPinterest);
            viewHolder.btnWhatsapp = (FloatingActionButton) view.findViewById(R.id.btnWhatsapp);
            viewHolder.menuShare = (FloatingActionsMenu) view.findViewById(R.id.menuShare);
            viewHolder.imageLike = (ImageView) view.findViewById(R.id.imageLike);
            viewHolder.imageDislkike = (ImageView) view.findViewById(R.id.imageDisLike);
            viewHolder.btnComment = (Button) view.findViewById(R.id.btnComment);
            viewHolder.btnShareMe = (Button) view.findViewById(R.id.btnShareMe);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            viewHolder.infoWrap = (LinearLayout) view.findViewById(R.id.info_wrap);
            viewHolder.openGraphView = (OpenGraphView) view.findViewById(R.id.og_view);
            viewHolder.textPreviewTitle = (TextView) viewHolder.openGraphView.findViewById(R.id.og_title);
            viewHolder.textPreviewDescription = (TextView) viewHolder.openGraphView.findViewById(R.id.og_description);
            viewHolder.textPreviewURL = (TextView) viewHolder.openGraphView.findViewById(R.id.og_url);
            viewHolder.imagePreviewThumb = (ImageView) viewHolder.openGraphView.findViewById(R.id.og_image);
            viewHolder.textCrawler  = new TextCrawler();

            //viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.btnLinkedIn.setTag(position);
        viewHolder.btnMail.setTag(position);
        viewHolder.btnFacebook.setTag(position);
        viewHolder.btnTwitter.setTag(position);
        viewHolder.btnGPlus.setTag(position);
        viewHolder.btnPinterest.setTag(position);
        viewHolder.btnWhatsapp.setTag(position);


        // Setting timeSpan
        viewHolder.textTime.setText(feedArrayList.get(position).getItemTimeAgo());

        viewHolder.textUserName.setText(feedArrayList.get(position).getUserFullName());

        String type=feedArrayList.get(position).getType();

        Log.e("BCUPAR",feedArrayList.get(position).getUserThumb()+"__"+position);
        // method to load company or user thumb
        if(!type.equals("file") && !type.equals("comment"))
            loadUserOrCompanyThumb(feedArrayList.get(position).getUserThumb(),viewHolder.imageUser, position);

        switch (type){
            case "file":
                viewHolder.layoutAction.setVisibility(View.VISIBLE);
                attachFileLayout(viewHolder,position,type);
                break;
            case "user":
                viewHolder.layoutAction.setVisibility(View.GONE);
                attachUserLayout(viewHolder,position);
                break;
            case "comment":
                viewHolder.layoutAction.setVisibility(View.VISIBLE);
                attachFileLayout(viewHolder,position,type);
                break;
            case "newsfeed":
                viewHolder.layoutAction.setVisibility(View.GONE);
                    attachShareLayout(viewHolder,position);
                break;
            default:
                viewHolder.layoutAction.setVisibility(View.GONE);
                break;
        }



        setClickListeners(position);


        viewHolder.imageLike.setVisibility(feedArrayList.get(position).isMenuExpanded() ? View.GONE : View.VISIBLE);
        viewHolder.imageDislkike.setVisibility(feedArrayList.get(position).isMenuExpanded() ? View.GONE : View.VISIBLE);
        viewHolder.btnComment.setVisibility(feedArrayList.get(position).isMenuExpanded() ? View.GONE : View.VISIBLE);
        //viewHolder.btnShareMe.setVisibility(feedArrayList.get(position).isMenuExpanded() ? View.GONE : View.VISIBLE);

        setVote(feedArrayList.get(position).getVoteStatus());

        viewHolder.menuShare.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                feedArrayList.get(position).setMenuExpanded(true);
                notifyDataSetChanged();
            }

            @Override
            public void onMenuCollapsed() {
                feedArrayList.get(position).setMenuExpanded(false);
                notifyDataSetChanged();
            }
        });



        return view;
    }

    private synchronized void loadUserOrCompanyThumb(String userThumbUrl, final ImageView imageView, final int position) {
        userThumbUrl=userThumbUrl.substring(1);
        userThumbUrl=AppConstants.URL_DOMAIN+userThumbUrl;

        Bitmap imageBitmap = mBitmapCache.get(userThumbUrl);

        if(imageBitmap!=null) {
            imageView.setImageBitmap(imageBitmap);
        }

        else {
            imageView.setImageBitmap(null);

            ImageLoader.getInstance()
                    .displayImage(userThumbUrl, imageView, optionsUserImage, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mBitmapCache.put(imageUri, loadedImage);
                            //notifyDataSetChanged();
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
/*
        Picasso.with(getContext()).load(userThumbUrl)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .into(imageView);
*/

    }

    private String randomNumber() {
        Random r = new Random();
        int Low = 10;
        int High = 10000;
        int Result = r.nextInt(High-Low) + Low;
        return Result+"";
    }

    private void setVote(int voteStatus) {

        if(voteStatus == 0){
            viewHolder.imageLike.setImageResource(R.drawable.ic_like_normal);
            viewHolder.imageDislkike.setImageResource(R.drawable.ic_dislike_normal);
        }

        else if(voteStatus == -1){
            viewHolder.imageLike.setImageResource(R.drawable.ic_like_normal);
            viewHolder.imageDislkike.setImageResource(R.drawable.ic_dislike);
        }

        else if(voteStatus == 1){
            viewHolder.imageLike.setImageResource(R.drawable.ic_like);
            viewHolder.imageDislkike.setImageResource(R.drawable.ic_dislike_normal);
        }
    }

    private void setClickListeners(final int position) {
        viewHolder.imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Comment> arrayList = feedArrayList.get(position).getCommentArrayList();
                if(feedArrayList.get(position).getCompanyId()==0) {
                    if(feedArrayList.get(position).getType().equalsIgnoreCase("comment") && !arrayList.get(0).getCommentText().contains("Sexy.."))
                        replaceCommentingUserFragment(position);
                    else
                        replaceUserFragment(position);
                }

                else{
                    if(feedArrayList.get(position).getType().equalsIgnoreCase("comment") && !arrayList.get(0).getCommentText().contains("Sexy.."))
                        replaceCommentingUserFragment(position);
                    else
                        replaceCompanyFragment(position);
                }
            }
        });

        viewHolder.textUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Comment> arrayList = feedArrayList.get(position).getCommentArrayList();
                if(feedArrayList.get(position).getCompanyId()==0) {
                    if(feedArrayList.get(position).getType().equalsIgnoreCase("comment") && !arrayList.get(0).getCommentText().contains("Sexy.."))
                        replaceCommentingUserFragment(position);
                    else
                        replaceUserFragment(position);
                }

                else{
                    if(feedArrayList.get(position).getType().equalsIgnoreCase("comment") && !arrayList.get(0).getCommentText().contains("Sexy.."))
                        replaceCommentingUserFragment(position);
                    else
                        replaceCompanyFragment(position);
                }
            }
        });

        viewHolder.imageUserFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedArrayList.get(position).getCompanyId()==0)
                    replaceUserFragment(position);
                else
                    replaceCompanyFragment(position);
            }
        });
        viewHolder.textUserNameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedArrayList.get(position).getCompanyId()==0)
                    replaceUserFragment(position);
                else
                    replaceCompanyFragment(position);
            }
        });


        viewHolder.imageFileThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload upload = getUploadObject(feedArrayList.get(position));
                Bundle bundle=new Bundle();
                int fileId;
                if (feedArrayList.get(position).getUploadArrayList().size()==0)
                    fileId = feedArrayList.get(position).getFileId();
                else
                    fileId = feedArrayList.get(position).getUploadArrayList().get(0).getFileId();
                bundle.putInt("fildeId",fileId);
                Fragment fragment=new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),((Activity) context),R.id.content_frame);
            }
        });

        viewHolder.imageShareThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload upload = getUploadObject(feedArrayList.get(position));
                Bundle bundle=new Bundle();
                int fileId;
                if (feedArrayList.get(position).getUploadArrayList().size()==0)
                    fileId = feedArrayList.get(position).getFileId();
                else
                    fileId = feedArrayList.get(position).getUploadArrayList().get(0).getFileId();
                bundle.putInt("fildeId",fileId);
                Fragment fragment=new FragmentFile();
                fragment.setArguments(bundle);
                MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),((Activity) context),R.id.content_frame);
            }
        });

        viewHolder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost(feedArrayList.get(position).getFileId()+"",AppConstants.USER_ID,viewHolder.imageLike,
                        viewHolder.imageDislkike, (Activity) context,position);
            }
        });

        viewHolder.imageDislkike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disLikePost(feedArrayList.get(position).getFileId()+"",AppConstants.USER_ID,viewHolder.imageLike,
                        viewHolder.imageDislkike, (Activity) context,position);
            }
        });


        viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = Utils.loadCommentDialog((Activity) context);
                TextView emptyText = (TextView) dialog.findViewById(R.id.emptyGridText);

                ListView listComments = (ListView) dialog.findViewById(R.id.listComments);
                commentArrayList = feedArrayList.get(position).getCommentArrayList();
                FragmentFile.fileOwnerId = feedArrayList.get(position).getUserId()+"";
                commentAdapter = new CommentAdapter(context,R.layout.row_comment, commentArrayList);
                listComments.setAdapter(commentAdapter);
                listComments.setEmptyView(emptyText);
                editComment = (EditText) dialog.findViewById(R.id.editComment);
                btnAddComment = (Button) dialog.findViewById(R.id.btnAddComment);
                btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                dialog.show();

                setAddCommentListener(position);
            }
        });


        viewHolder.btnFacebook.setOnClickListener(this);
        viewHolder.btnGPlus.setOnClickListener(this);
        viewHolder.btnLinkedIn.setOnClickListener(this);
        viewHolder.btnMail.setOnClickListener(this);
        viewHolder.btnPinterest.setOnClickListener(this);
        viewHolder.btnTwitter.setOnClickListener(this);
        viewHolder.btnWhatsapp.setOnClickListener(this);
    }

    private void replaceCompanyFragment(int position) {
        FragmentCompany.companyId = feedArrayList.get(position).getCompanyId();
        Fragment fragment=new FragmentCompany();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOwnCompany", false);
        bundle.putInt(AppConstants.KEY_COMPANY_ID,feedArrayList.get(position).getCompanyId());
        fragment.setArguments(bundle);
        replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);

    }

    private void setAddCommentListener(final int position) {

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment= editComment.getText().toString();
                Log.e("CCheck", "onClick: "+comment );
                if(comment.equals("")){
                    editComment.setError("Please write a comment first");
                    editComment.requestFocus();
                    return;
                }

                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                String timeDate = formatter1.format(date);

                addComment("0",feedArrayList.get(position).getFileId()+"",AppConstants.USER_ID,comment,timeDate,timeDate, (Activity) context);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void attachShareLayout(ViewHolder viewHolder, int position) {
        ArrayList<Upload> uploadArrayList = feedArrayList.get(position).getUploadArrayList();
        viewHolder.layoutPreview.setVisibility(View.GONE);

        if(uploadArrayList.size()>1){
            viewHolder.layoutUser.setVisibility(View.GONE);
            viewHolder.layoutFileComment.setVisibility(View.GONE);
            viewHolder.layoutShare.setVisibility(View.GONE);
            viewHolder.layoutAction.setVisibility(View.VISIBLE);
            viewHolder.textMultipleFiles.setText(uploadArrayList.get(0).getDescription());
            viewHolder.layoutMultipleFiles.setVisibility(View.VISIBLE);
            viewHolder.textView.setText("shared some info");

            setRecyclerView(uploadArrayList);
        }

        else{
            viewHolder.layoutUser.setVisibility(View.GONE);
            viewHolder.layoutFileComment.setVisibility(View.GONE);
            viewHolder.layoutShare.setVisibility(View.VISIBLE);
            //viewHolder.textCategoryName.setVisibility(View.GONE);
            viewHolder.layoutMultipleFiles.setVisibility(View.GONE);
            viewHolder.textShare.setText(feedArrayList.get(position).getComment());
            viewHolder.textView.setText("shared some info");

            if(uploadArrayList.size()>0) {
                viewHolder.imageShareThumb.setVisibility(View.VISIBLE);

                // If url shared from newsfeed
                if (uploadArrayList.get(0).getFileType().equals("videourl")) {
                    viewHolder.layoutAction.setVisibility(View.VISIBLE);
                    String videoId = extractVideoIdFromYoutube(uploadArrayList.get(0).getVideoURL());
                    String url = "http://img.youtube.com/vi/"+videoId+"/0.jpg";
                    //Picasso.with(getContext()).load(url).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageShareThumb);

                    Bitmap imageBitmap = mBitmapCache.get(url);
                    if(imageBitmap!=null)
                        viewHolder.imageShareThumb.setImageBitmap(imageBitmap);
                    else
                        ImageLoader.getInstance().displayImage(url, viewHolder.imageShareThumb, optionsFile, imageFileLoadingListener);

                } else {
                    Bitmap imageBitmap = mBitmapCache.get(uploadArrayList.get(0).getThumbURL());
                    if(imageBitmap!=null)
                        viewHolder.imageShareThumb.setImageBitmap(imageBitmap);
                    else
                        ImageLoader.getInstance()
                                .displayImage(uploadArrayList.get(0).getThumbURL(), viewHolder.imageShareThumb, optionsFile, imageFileLoadingListener);

                    //Picasso.with(getContext()).load(uploadArrayList.get(0).getThumbURL()).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageShareThumb);
                }
            }

            else{
                // When shared text is not url only without any upload file
                if(!feedArrayList.get(position).getComment().contains("http") && !feedArrayList.get(position).getComment().contains("www."))
                    viewHolder.imageShareThumb.setVisibility(View.GONE);
                else{
                    Log.e("check",feedArrayList.get(position).getComment());
                    initPreviewLayout(viewHolder,position, feedArrayList.get(position).getComment());
                }
            }
        }
    }

    private String extractVideoIdFromYoutube(String videoURL) {
        String videoId = "1111";
        if(!videoURL.contains("youtube"))
            return videoId;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoURL);

        if(matcher.find())
            return matcher.group();
        else
            return videoId;
    }

    private void initPreviewLayout(final ViewHolder viewHolder, final int position, String url) {
        viewHolder.layoutUser.setVisibility(View.GONE);
        viewHolder.layoutFileComment.setVisibility(View.GONE);
        viewHolder.layoutShare.setVisibility(View.GONE);
        viewHolder.layoutPreview.setVisibility(View.VISIBLE);
        viewHolder.layoutMultipleFiles.setVisibility(View.GONE);
        viewHolder.textShare.setVisibility(View.GONE);
        viewHolder.textView.setText("shared some info");


        viewHolder.layoutPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedArrayList.get(position).getComment()));
                    ((Activity)context).startActivity(myIntent);
                }catch (ActivityNotFoundException e){
                    MyApplication.alertDialog((Activity)context, "No Compatible App Found to View Page", "App Not Found");
                }
            }
        });

        // FOr fixing youtube thumb making issue
        if (url.contains("youtu.be"))
            url = url.replace("http:","https:");

        if(!url.contains("24.com") && !url.contains("24.co")){
            viewHolder.progressBar.setVisibility(View.GONE);
            if (!feedArrayList.get(position).isPreviewLoaded()) {
                Log.e("AAAAA",url);
                //feedArrayList.get(position).setPreviewLoaded(true);
                OnLoadListener onLoadListener = new OnLoadListener() {
                    @Override
                    public void onLoadFinish(OGData ogData) {
                        feedArrayList.get(position).setPreviewTitle(ogData.getTitle());
                        feedArrayList.get(position).setPreviewDescription(ogData.getDescription());
                        feedArrayList.get(position).setPreviewPageURL(ogData.getUrl());
                        feedArrayList.get(position).setPreviewThumbURL(ogData.getImage());
                        feedArrayList.get(position).setPreviewLoaded(true);
                        notifyDataSetChanged();
                    }
                };

                viewHolder.openGraphView.setOnLoadListener(onLoadListener);
                viewHolder.openGraphView.loadFrom(url);
            }

            else{
                viewHolder.openGraphView.loadFrom(url);
            }

        }

        else {

            if (!feedArrayList.get(position).isPreviewLoaded()) {
                LinkPreviewCallback callback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        viewHolder.openGraphView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean isNull) {
                        if (feedArrayList.size() != 0){
                            viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.openGraphView.setVisibility(View.VISIBLE);
                            feedArrayList.get(position).setPreviewLoaded(true);
                            feedArrayList.get(position).setPreviewTitle(sourceContent.getTitle());
                            feedArrayList.get(position).setPreviewDescription(sourceContent.getDescription());
                            feedArrayList.get(position).setPreviewPageURL(sourceContent.getUrl());

                            notifyDataSetChanged();

                            if (sourceContent.getImages().size()>0)
                                feedArrayList.get(position).setPreviewThumbURL(sourceContent.getImages().get(0));

                            viewHolder.textPreviewTitle.setText(sourceContent.getTitle());
                            viewHolder.textPreviewDescription.setText(sourceContent.getDescription());
                            viewHolder.textPreviewURL.setText(sourceContent.getUrl());

                                Bitmap imageBitmap = mBitmapCache.get(sourceContent.getImages().get(0));
                                if (imageBitmap != null)
                                    viewHolder.imagePreviewThumb.setImageBitmap(imageBitmap);
                                else
                                    ImageLoader.getInstance()
                                            .displayImage(sourceContent.getImages().get(0), viewHolder.imagePreviewThumb, optionsFile, imageFileLoadingListener);

                        }
                    }
                };
                viewHolder.textCrawler
                        .makePreview(callback, url);

            }
            else{
                //viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.textPreviewTitle.setText(feedArrayList.get(position).getPreviewTitle());
                viewHolder.textPreviewDescription.setText(feedArrayList.get(position).getPreviewDescription());
                viewHolder.textPreviewURL.setText(feedArrayList.get(position).getPreviewPageURL());

                //Picasso.with(getContext()).load(feedArrayList.get(position).getPreviewThumbURL()).error(android.R.drawable.stat_notify_error).into(viewHolder.imagePreviewThumb);
                Bitmap imageBitmap = mBitmapCache.get(feedArrayList.get(position).getPreviewThumbURL());
                if(imageBitmap!=null)
                    viewHolder.imagePreviewThumb.setImageBitmap(imageBitmap);
/*
                else
                    ImageLoader.getInstance()
                            .displayImage(feedArrayList.get(position).getPreviewThumbURL(), viewHolder.imagePreviewThumb, optionsFile, imageFileLoadingListener);
*/

            }

        }



    }

    public boolean updateURLView(int position, String title, String description, String url, String image) {
        int first = listFeeds.getFirstVisiblePosition();
        int last = listFeeds.getLastVisiblePosition();
        if(position < first || position > last) {
            //just update your DataSet
            //the next time getView is called
            //the ui is updated automatically
            return false;
        }
        else {
            View convertView = listFeeds.getChildAt(position - first);
            //this is the convertView that you previously returned in getView
            //just fix it (for example:)
            OpenGraphView openGraphView = (OpenGraphView) convertView.findViewById(R.id.og_view);
            TextView textPreviewTitle = (TextView) openGraphView.findViewById(R.id.og_title);
            TextView textPreviewDescription = (TextView) openGraphView.findViewById(R.id.og_description);
            TextView textPreviewURL = (TextView) openGraphView.findViewById(R.id.og_url);
            ImageView imagePreviewThumb = (ImageView) openGraphView.findViewById(R.id.og_image);
            textPreviewTitle.setText(title);
            textPreviewDescription.setText(description);
            textPreviewURL.setText(url);


            return true;
        }
    }

    private void replaceUserFragment(int position) {
        Fragment fragment=new FragmentUser();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_USER_ID,feedArrayList.get(position).getUserId()+"");
        bundle.putString(AppConstants.KEY_USER_NAME,feedArrayList.get(position).getUserFullName());
        fragment.setArguments(bundle);

        if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUser){
            Intent i = new Intent((Activity) context,MainActivity.class);
            i.putExtra("startUserWrapper",true);
            i.putExtra(AppConstants.KEY_USER_ID,feedArrayList.get(position).getUserId()+"");
            i.putExtra(AppConstants.KEY_USER_NAME,feedArrayList.get(position).getUserFullName());
            ((Activity) context).startActivity(i);
            ((Activity) context).finish();
        }
        else
            MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);
    }
    private void replaceCommentingUserFragment(int position) {
        ArrayList<Comment> arrayList = feedArrayList.get(position).getCommentArrayList();

        Fragment fragment=new FragmentUser();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_USER_ID,arrayList.get(0).getUserId()+"");
        bundle.putString(AppConstants.KEY_USER_NAME,arrayList.get(0).getUserFullName());
        fragment.setArguments(bundle);

        if(((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentUser){
            Intent i = new Intent((Activity) context,MainActivity.class);
            i.putExtra("startUserWrapper",true);
            i.putExtra(AppConstants.KEY_USER_ID,arrayList.get(0).getUserId()+"");
            i.putExtra(AppConstants.KEY_USER_NAME,arrayList.get(0).getUserFullName());
            ((Activity) context).startActivity(i);
            ((Activity) context).finish();
        }
        else
            MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);
    }

    private Upload getUploadObject(Feed feed) {
        Upload upload = new Upload();
        upload.setFileId(feed.getFileId());
        upload.setDownloadsCount(feed.getDownloadsCount());
        upload.setViewsCount(feed.getViewsCount());
        upload.setFileType(feed.getFileType());
        upload.setFileType(feed.getFileType());
        upload.setVideoURL(feed.getVideoUrl());
        String thumbName = feed.getThumbFileName();
        upload.setThumbURL(feed.getThumbURL());
        upload.setFileURL(createFileURL(feed));
        upload.setOwnerId(feed.getUserId());
        upload.setUserFullName(feed.getUserFullName());
        return upload;
    }

    private void attachUserLayout(ViewHolder viewHolder,int position) {
        viewHolder.layoutUser.setVisibility(View.VISIBLE);
        viewHolder.layoutShare.setVisibility(View.GONE);
        viewHolder.layoutFileComment.setVisibility(View.GONE);
        viewHolder.layoutMultipleFiles.setVisibility(View.GONE);
        viewHolder.layoutPreview.setVisibility(View.GONE);

        viewHolder.textView.setText("joined SwopInfo.com");

    }

    private void attachFileLayout(ViewHolder viewHolder, int position, String type) {
        viewHolder.layoutUser.setVisibility(View.GONE);
        viewHolder.layoutShare.setVisibility(View.GONE);
        viewHolder.layoutFileComment.setVisibility(View.VISIBLE);
        viewHolder.layoutMultipleFiles.setVisibility(View.GONE);
        viewHolder.layoutPreview.setVisibility(View.GONE);
        viewHolder.textComment.setText(feedArrayList.get(position).getComment());

        if(type.equals("file")) {
            viewHolder.layoutAdditionalInfo.setVisibility(View.GONE);
            viewHolder.textComment.setText(feedArrayList.get(position).getComment());

            // Condition to load file thumb image on basis of file type whether video url or image
            if(feedArrayList.get(position).getFileType().equals("videourl")) {
                //Picasso.with(getContext()).load(feedArrayList.get(position).getThumbFileName()).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageFileThumb);
                Bitmap imageBitmap = mBitmapCache.get(feedArrayList.get(position).getThumbFileName());
                if(imageBitmap!=null)
                    viewHolder.imageFileThumb.setImageBitmap(imageBitmap);
                else
                    ImageLoader.getInstance()
                            .displayImage(feedArrayList.get(position).getThumbFileName(), viewHolder.imageFileThumb, optionsFile, imageFileLoadingListener);

            }
            else {
                String fileThumbUrl=feedArrayList.get(position).getThumbFileName();
                fileThumbUrl=fileThumbUrl.substring(1);
                fileThumbUrl=AppConstants.URL_DOMAIN+fileThumbUrl;
                //Picasso.with(getContext()).load(fileThumbUrl).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageFileThumb);
                Bitmap imageBitmap = mBitmapCache.get(fileThumbUrl);
                if(imageBitmap!=null)
                    viewHolder.imageFileThumb.setImageBitmap(imageBitmap);
                else
                    ImageLoader.getInstance()
                            .displayImage(fileThumbUrl, viewHolder.imageFileThumb, optionsFile, imageFileLoadingListener);
            }

            viewHolder.textView.setText("uploaded a new file");

            // if file is uploaded via company then set company else username
            if(feedArrayList.get(position).getCompanyId()!=0){
                viewHolder.textUserName.setText(feedArrayList.get(position).getCompanyName());
                loadUserOrCompanyThumb(feedArrayList.get(position).getCompanyThumbFileName(),viewHolder.imageUser, position);
            }
            else{
                loadUserOrCompanyThumb(feedArrayList.get(position).getUserThumb(),viewHolder.imageUser, position);
                viewHolder.textUserName.setText(feedArrayList.get(position).getUserFullName());
            }
        }

        else if(type.equals("comment")){
            viewHolder.textTimeFile.setText(feedArrayList.get(position).getFileTime());
            ArrayList<Comment> arrayList = feedArrayList.get(position).getCommentArrayList();

            ArrayList<Comment> minimizedList = calculateCommentingUsers(arrayList);

            Log.e("QQQQQ", "attachFileLayout: "+arrayList.size()+"   "+ arrayList.get(0).getUserFullName());
            switch (minimizedList.size()){
                case 1:
                    viewHolder.textComment.setText(arrayList.get(0).getUserFullName()+" commented on this.");
                    break;
                case 2:
                    viewHolder.textComment.setText(arrayList.get(0).getUserFullName()+" and "+arrayList.get(1).getUserFullName()
                            +" commented on this.");
                    break;
                default:
                    viewHolder.textComment.setText(arrayList.get(0).getUserFullName()+" and "+(minimizedList.size()-1)+" others commented on this");
            }

            viewHolder.layoutAdditionalInfo.setVisibility(View.VISIBLE);
            viewHolder.textUserName.setText(arrayList.get(0).getUserFullName());

            Bitmap imageBitmap = mBitmapCache.get(arrayList.get(0).getUserImageURL());
            if(imageBitmap!=null) {
                Log.e("BmpCache","comment_"+arrayList.get(0).getUserImageURL());
                viewHolder.imageUser.setImageBitmap(imageBitmap);
            }

            else {

                ImageLoader.getInstance()
                        .displayImage(arrayList.get(0).getUserImageURL(), viewHolder.imageUser, optionsUserImage, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                mBitmapCache.put(imageUri, loadedImage);
                                //notifyDataSetChanged();
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
            }

            //Picasso.with(getContext()).load(arrayList.get(0).getUserImageURL()).placeholder(R.drawable.avtar).error(R.drawable.avtar).into(viewHolder.imageUser);
            viewHolder.textViewFile.setText("uploaded a file");
            viewHolder.textView.setText("added a comment to");
            //viewHolder.textCategoryName.setVisibility(View.GONE);

            // Condition to load file thumb image on basis of file type whether video url or image
            if(feedArrayList.get(position).getFileType().equals("videourl")) {
                Bitmap imageBitmap1 = mBitmapCache.get(feedArrayList.get(position).getThumbFileName());
                if(imageBitmap1!=null)
                    viewHolder.imageFileThumb.setImageBitmap(imageBitmap1);
                else
                    ImageLoader.getInstance()
                            .displayImage(feedArrayList.get(position).getThumbFileName(), viewHolder.imageFileThumb, optionsFile, imageFileLoadingListener);

                //Picasso.with(getContext()).load(feedArrayList.get(position).getThumbFileName()).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageFileThumb);
            }
            else {
                String fileThumbUrl=feedArrayList.get(position).getThumbFileName();
                fileThumbUrl=fileThumbUrl.substring(1);
                fileThumbUrl=AppConstants.URL_DOMAIN+fileThumbUrl;
                Bitmap imageBitmap1 = mBitmapCache.get(fileThumbUrl);
                if(imageBitmap1!=null)
                    viewHolder.imageFileThumb.setImageBitmap(imageBitmap1);
                else
                    ImageLoader.getInstance()
                            .displayImage(fileThumbUrl, viewHolder.imageFileThumb, optionsFile, imageFileLoadingListener);

                //Picasso.with(getContext()).load(fileThumbUrl).placeholder(R.drawable.document_gray).error(android.R.drawable.stat_notify_error).into(viewHolder.imageFileThumb);
            }

            // if file is uploaded via company then set company else username
            if(feedArrayList.get(position).getCompanyId()!=0){
                viewHolder.textUserNameFile.setText(feedArrayList.get(position).getCompanyName());
                Log.e("BHENCHODU",feedArrayList.get(position).getCompanyThumbFileName());
                loadUserOrCompanyThumb(feedArrayList.get(position).getCompanyThumbFileName(),viewHolder.imageUserFile,position);
            }
            else {
                Log.e("BHENCHODC",feedArrayList.get(position).getUserThumb());
                loadUserOrCompanyThumb(feedArrayList.get(position).getUserThumb(),viewHolder.imageUserFile,position);
                viewHolder.textUserNameFile.setText(feedArrayList.get(position).getUserFullName());
            }
        }
    }

    private ArrayList<Comment>  calculateCommentingUsers(ArrayList<Comment> arrayList) {
        ArrayList<Integer> commentingUserIds = new ArrayList<>();
        ArrayList<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if(!commentingUserIds.contains(arrayList.get(i).getUserId())){
                commentingUserIds.add(arrayList.get(i).getUserId());
                commentList.add(arrayList.get(i));
            }
        }
        return commentList;
    }

    public String createFileURL(Feed feed) {
        String folderName="";
        if(!feed.getFolderName().equals("/"))
            folderName = feed.getFolderName().replace(" ","%20")+"/";

        String fileURL = AppConstants.URL_DOMAIN+"upload/user"+feed.getFileUserId()+"/"+folderName+feed.getFileName();
        Log.e("FILEURL",fileURL);
        return fileURL;
    }


    @Override
    public void onClick(View view) {
        fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+feedArrayList.get((Integer) view.getTag()).getFileId();
        switch (view.getId()){

            case R.id.btnLinkedIn:
                new Utils((Activity) context).sharePost("com.linkedin.android","Linkedin",fileURLtoShare);
                break;

            case R.id.btnMail:
                new Utils((Activity) context).shareOnMails(fileURLtoShare);
                break;

            case R.id.btnGPlus:
                new Utils((Activity) context).sharePost("com.google.android.apps.plus","Google Plus",fileURLtoShare);
                break;

            case R.id.btnFacebook:
                new Utils((Activity) context).sharePost("com.facebook.katana","Facebook",fileURLtoShare);
                break;

            case R.id.btnTwitter:
                new Utils((Activity) context).sharePost("com.twitter.android","Twitter",fileURLtoShare);
                break;

            case R.id.btnWhatsapp:
                new Utils((Activity) context).sharePost("com.whatsapp","Whatsapp",fileURLtoShare);
                break;

            case R.id.btnPinterest:
                new Utils((Activity) context).sharePost("com.pinterest","Pinterest",fileURLtoShare);
                break;
        }
    }

    private void setRecyclerView(ArrayList<Upload> uploadArrayList) {
        StaggeredGridLayoutManager staggeredGridLayoutManager;

        viewHolder.recyclerView.setHasFixedSize(true);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        viewHolder.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        MultipleFilesRecyclerViewAdapter rcAdapter = new MultipleFilesRecyclerViewAdapter((Activity)context, uploadArrayList);
        viewHolder.recyclerView.setAdapter(rcAdapter);
    }

    public void likePost(String fileId, String userId, final ImageView imageLike, final ImageView imageDisLike, final Activity activity, final int position) {
        String url=AppConstants.URL_FILE + fileId+"/voteup/"+userId;
        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"test"},new String[]{"test"});

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
                                feedArrayList.get(position).setVoteStatus(1);
                                notifyDataSetChanged();
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

    public void disLikePost(String fileId, String userId, final ImageView imageLike, final ImageView imageDisLike, final Activity activity, final int position) {
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
                                feedArrayList.get(position).setVoteStatus(-1);
                                notifyDataSetChanged();
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

    private void addComment(String commentId, String fileId, String userId, final String commentText, String timeDate, String timeDate1, final Activity activity) {
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
                                FragmentHome.feedArrayList=new ArrayList<>();
                                new FragmentHome().getFeeds(activity, FragmentHome.listFeeds,FragmentHome.swipeRefreshLayout);

                                Snackbar.make(activity.findViewById(android.R.id.content),"Comment is added successfully", Snackbar.LENGTH_LONG).show();
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
                                commentAdapter.notifyDataSetChanged();
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



    public ImageLoadingListener imageFileLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            ((ImageView) view).setImageResource(R.drawable.app_logo);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            mBitmapCache.put(imageUri,loadedImage);
            //notifyDataSetChanged();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}