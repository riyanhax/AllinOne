package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.mobicomkit.api.conversation.Message;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class AllFilesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView countryPhoto;
    public TextView textViews, textDownloads, textScore, textDownload;
    public ArrayList<Upload> allFilesList=new ArrayList<>();
    public ImageView imageDisLike, imageLike, imageShare, imageComment, imageShareToChat;
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
        imageComment = (ImageView) itemView.findViewById(R.id.imgComment);
        imageShare = (ImageView) itemView.findViewById(R.id.imgShareToApp);
        imageShareToChat = (ImageView) itemView.findViewById(R.id.imgShareToChat);

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

        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                Utils.shareURLCustomIntent(fileURLtoShare, (Activity) context);
            }
        });

        imageShareToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fileURLtoShare = AppConstants.URL_DOMAIN+"docview.aspx?fileid="+allFilesList.get(getPosition()).getFileId();
                MainActivity mainActivity = (MainActivity) context;
                Message message = mainActivity.buildMessages(null, fileURLtoShare, (Activity) context);
                mainActivity.startMessageForward(message, (Activity) context);

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
