package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.fragment.FragmentAllFiles;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.model.Upload;

import java.util.ArrayList;
import java.util.List;

import me.kaelaela.opengraphview.OpenGraphView;

public class FeedViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Context context;
    TextView textUserName,textFolderName,textView,textComment,textShare,textUserNameFile,textViewFile,textTime,textTimeFile,
            textMultipleFiles,textPreviewTitle, textPreviewDescription,textPreviewURL;
    ImageView imageUser,imageFileThumb,imageLike,imageDislkike,imageUserFile,imageShareThumb,imagePreviewThumb;
    LinearLayout layoutFileComment,layoutUser,layoutShare,layoutAction,layoutAdditionalInfo,layoutMultipleFiles;
    FloatingActionButton btnLinkedIn,btnMail,btnFacebook,btnTwitter,btnGPlus,btnPinterest,btnWhatsapp;
    FloatingActionsMenu menuShare;
    Button btnComment,btnShareMe;
    RecyclerView recyclerView;

    ProgressBar progressBar;
    // Preview
    LinearLayout layoutPreview,infoWrap,layoutPreviewContent;
    View loading;;
    OpenGraphView openGraphView;

    public FeedViewHolders(View view, Context context, List<Feed> feedList) {
        super(view);
        this.context = context;
        textUserName = (TextView) view.findViewById(R.id.textUserName);
        textUserNameFile = (TextView) view.findViewById(R.id.textUserNameFile);
        textView = (TextView) view.findViewById(R.id.textView);
        textViewFile = (TextView) view.findViewById(R.id.textViewFile);
        textComment = (TextView) view.findViewById(R.id.textComment);
        textShare = (TextView) view.findViewById(R.id.textShared);
        textTime = (TextView) view.findViewById(R.id.textTime);
        textTimeFile = (TextView) view.findViewById(R.id.textTimeFile);
        textMultipleFiles = (TextView) view.findViewById(R.id.textMultipleFiles);
        imageUser = (ImageView) view.findViewById(R.id.imageUser);
        imageUserFile = (ImageView) view.findViewById(R.id.imageUserFile);
        imageFileThumb = (ImageView) view.findViewById(R.id.imageFileThumb);
        imageShareThumb = (ImageView) view.findViewById(R.id.imageShareThumb);
        layoutFileComment = (LinearLayout) view.findViewById(R.id.layoutFileComment);
        layoutPreview = (LinearLayout) view.findViewById(R.id.layoutPreview);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_multiple_files);
        layoutAdditionalInfo = (LinearLayout) view.findViewById(R.id.layoutAdditionalInfo);
        layoutMultipleFiles = (LinearLayout) view.findViewById(R.id.layoutMultipleFiles);
        layoutShare = (LinearLayout) view.findViewById(R.id.layoutShare);
        layoutUser = (LinearLayout) view.findViewById(R.id.layoutUser);
        layoutAction = (LinearLayout) view.findViewById(R.id.layoutActions);
        btnLinkedIn = (FloatingActionButton) view.findViewById(R.id.btnLinkedIn);
        btnMail = (FloatingActionButton) view.findViewById(R.id.btnMail);
        btnFacebook = (FloatingActionButton) view.findViewById(R.id.btnFacebook);
        btnTwitter = (FloatingActionButton) view.findViewById(R.id.btnTwitter);
        btnGPlus = (FloatingActionButton) view.findViewById(R.id.btnGPlus);
        btnPinterest = (FloatingActionButton) view.findViewById(R.id.btnPinterest);
        btnWhatsapp = (FloatingActionButton) view.findViewById(R.id.btnWhatsapp);
        menuShare = (FloatingActionsMenu) view.findViewById(R.id.menuShare);
        imageLike = (ImageView) view.findViewById(R.id.imageLike);
        imageDislkike = (ImageView) view.findViewById(R.id.imageDisLike);
        btnComment = (Button) view.findViewById(R.id.btnComment);
        btnShareMe = (Button) view.findViewById(R.id.btnShareMe);
        //progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        infoWrap = (LinearLayout) view.findViewById(R.id.info_wrap);
        openGraphView = (OpenGraphView) view.findViewById(R.id.og_view);
        textPreviewTitle = (TextView) openGraphView.findViewById(R.id.og_title);
        textPreviewDescription = (TextView) openGraphView.findViewById(R.id.og_description);
        textPreviewURL = (TextView) openGraphView.findViewById(R.id.og_url);
        imagePreviewThumb = (ImageView) openGraphView.findViewById(R.id.og_image);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

    }

    @Override
    public void onClick(View view) {
        Bundle bundle=new Bundle();
//        bundle.putSerializable("uploadList",uploadList);
        Fragment fragment=new FragmentAllFiles();
        fragment.setArguments(bundle);
        MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),((Activity) context),R.id.content_frame);

    }
}
