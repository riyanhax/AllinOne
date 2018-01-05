package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

public class AllFilesRecyclerViewAdapter extends RecyclerView.Adapter<AllFilesViewHolders> {

    private List<Upload> itemList;
    private Context context;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    public AllFilesRecyclerViewAdapter(Context context, List<Upload> itemList) {
        this.itemList = itemList;
        this.context = context;
        for (int i = 0; i < itemList.size(); i++) {
            bitmapArrayList.add(null);
        }
    }

    @Override
    public AllFilesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_files, null);
        AllFilesViewHolders rcv = new AllFilesViewHolders(layoutView,itemList, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final AllFilesViewHolders holder, final int position) {
        holder.textDownloads.setText(itemList.get(position).getDownloadsCount()+"");
        holder.textViews.setText(itemList.get(position).getViewsCount()+"");
        holder.textScore.setText(itemList.get(position).getScore()+"");

        Log.e("URL",itemList.get(position).getFileURL());
//        Picasso.with(context).load(itemList.get(position).getFileURL()).placeholder(R.drawable.document_gray).error(R.drawable.document_gray).into(holder.countryPhoto);

        loadFileThumb(position, holder);


        setUserVote(itemList.get(position).getUserVote(), holder);

        holder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageLike.setImageResource(R.drawable.ic_like);
                holder.imageDisLike.setImageResource(R.drawable.ic_dislike_normal);
                likePost(itemList.get(position).getFileId()+"", AppConstants.USER_ID, (Activity) context, position, holder);
            }
        });
        holder.imageDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageDisLike.setImageResource(R.drawable.ic_dislike);
                holder.imageLike.setImageResource(R.drawable.ic_like_normal);
                disLikePost(itemList.get(position).getFileId()+"", AppConstants.USER_ID, (Activity) context, position, holder);
            }
        });

        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Dialog dialog = Utils.loadCommentDialog((Activity) context);
                TextView emptyText = (TextView) dialog.findViewById(R.id.emptyGridText);

                ListView listComments = (ListView) dialog.findViewById(R.id.listComments);
                ArrayList<Comment> commentArrayList = itemList.get(position).getCommentArrayList();
                FragmentFile.fileOwnerId = feedArrayList.get(position).getUserId()+"";
                commentAdapter = new CommentAdapter(context,R.layout.row_comment, commentArrayList);
                listComments.setAdapter(commentAdapter);
                listComments.setEmptyView(emptyText);
                editComment = (EditText) dialog.findViewById(R.id.editComment);
                btnAddComment = (Button) dialog.findViewById(R.id.btnAddComment);
                btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                dialog.show();

                setAddCommentListener(position);
*/

            }
        });
    }

    private void setUserVote(int userVote, AllFilesViewHolders holder) {
        switch (userVote){
            case 1:
                holder.imageLike.setImageResource(R.drawable.ic_like);
                holder.imageDisLike.setImageResource(R.drawable.ic_dislike_normal);
                holder.imageLike.setClickable(false);
                holder.imageDisLike.setClickable(true);
                break;
            case -1:
                holder.imageDisLike.setImageResource(R.drawable.ic_dislike);
                holder.imageLike.setImageResource(R.drawable.ic_like_normal);
                holder.imageDisLike.setClickable(false);
                holder.imageLike.setClickable(true);
                break;
        }
    }


    private void loadFileThumb(final int position, final AllFilesViewHolders holder) {

        String url = itemList.get(position).getThumbURL();
//        String url = itemList.get(position).getFileType().contains("image") ? itemList.get(position).getFileURL() : itemList.get(position).getThumbURL();
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.app_logo)
//                .resize(holder.countryPhoto.getWidth(),300)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.countryPhoto);

    }

    @Override
    public int getItemCount() {
            return this.itemList.size();
    }

    public void likePost(final String fileId, String userId, final Activity activity, final int position, final AllFilesViewHolders holder) {
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
                                int currentVote = itemList.get(position).getUserVote();
                                int score = itemList.get(position).getScore();
                                if(currentVote == 0)
                                    itemList.get(position).setScore(score+1);
                                else if(currentVote == -1)
                                    itemList.get(position).setScore(score+2);

                                itemList.get(position).setUserVote(1);
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

    private void disLikePost(final String fileId, String userId, final Activity activity, final int position, final AllFilesViewHolders holder) {
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
                                int currentVote = itemList.get(position).getUserVote();
                                int score = itemList.get(position).getScore();
                                if(currentVote == 0)
                                    itemList.get(position).setScore(score-1);
                                else if(currentVote == 1)
                                    itemList.get(position).setScore(score-2);

                                itemList.get(position).setUserVote(-1);
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

}