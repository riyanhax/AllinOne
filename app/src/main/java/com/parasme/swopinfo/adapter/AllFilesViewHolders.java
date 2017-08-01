package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentAllFiles;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class AllFilesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView countryPhoto;
    public TextView textViews, textDownloads, textScore, textDownload;
    public ArrayList<Upload> allFilesList=new ArrayList<>();
    public FloatingActionButton btnLinkedIn,btnMail,btnFacebook,btnTwitter,btnGPlus,btnPinterest,btnWhatsapp;
    public FloatingActionsMenu menuShare;
    public ImageView imageDisLike, imageLike;
    public Button btnComment;
    public Context context;

    public AllFilesViewHolders(View itemView, final List<Upload> allFilesList, final Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        countryPhoto = (ImageView) itemView.findViewById(R.id.imageShareThumb);
        textViews = (TextView) itemView.findViewById(R.id.textViewCount);
        textDownloads = (TextView) itemView.findViewById(R.id.textDownloadCount);
        textDownload = (TextView) itemView.findViewById(R.id.textDownload);
        textScore = (TextView) itemView.findViewById(R.id.textScore);
        imageDisLike = (ImageView) itemView.findViewById(R.id.imageDisLike);
        imageLike = (ImageView) itemView.findViewById(R.id.imageLike);
        btnComment = (Button) itemView.findViewById(R.id.btnComment);

        btnLinkedIn = (FloatingActionButton) itemView.findViewById(R.id.btnLinkedIn);
        btnMail = (FloatingActionButton) itemView.findViewById(R.id.btnMail);
        btnFacebook = (FloatingActionButton) itemView.findViewById(R.id.btnFacebook);
        btnTwitter = (FloatingActionButton) itemView.findViewById(R.id.btnTwitter);
        btnGPlus = (FloatingActionButton) itemView.findViewById(R.id.btnGPlus);
        btnPinterest = (FloatingActionButton) itemView.findViewById(R.id.btnPinterest);
        btnWhatsapp = (FloatingActionButton) itemView.findViewById(R.id.btnWhatsapp);
        menuShare = (FloatingActionsMenu) itemView.findViewById(R.id.menuShare);

        this.allFilesList.addAll(allFilesList);

        setShareListeners();

        this.context = context;
        textDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.downloadFile(allFilesList.get(getPosition()).getFileURL(),(Activity) context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setShareListeners() {

        btnLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.linkedin.android","Linkedin",fileURLtoShare);
            }
        });
        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).shareOnMails(fileURLtoShare);
            }
        });
        btnGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.google.android.apps.plus","Google Plus",fileURLtoShare);
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.facebook.katana","Facebook",fileURLtoShare);
            }
        });
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.twitter.android","Twitter",fileURLtoShare);
            }
        });
        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.whatsapp","Whatsapp",fileURLtoShare);            }
        });
        btnPinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                new Utils((Activity) context).sharePost("com.pinterest","Pinterest",fileURLtoShare);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Bundle bundle=new Bundle();
        bundle.putInt("fildeId",allFilesList.get(getPosition()).getFileId());
        Fragment fragment=new FragmentFile();
        fragment.setArguments(bundle);
        MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),((Activity) context),R.id.content_frame);
    }
}
