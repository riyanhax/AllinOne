package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

/**
 * Created by SoNu on 6/6/2016.
 */

public class CommentAdapter extends ArrayAdapter<Upload> {

    private int resourceId;
    private ArrayList<Comment> commentArrayList;
    private Context context;
    private ViewHolder viewHolder;
    LayoutInflater vi;

    public CommentAdapter(Context context, int resourceId, ArrayList<Comment> commentArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.commentArrayList = commentArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return commentArrayList.size();
    }


    class ViewHolder
    {
        TextView textUserName, textDate;
        EditText editComment;
        ImageView imageUser,imageDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textUserName = (TextView) view.findViewById(R.id.text_user_name);
            viewHolder.editComment = (EditText) view.findViewById(R.id.text_comment);
            viewHolder.imageUser = (ImageView) view.findViewById(R.id.image_user);
            viewHolder.imageDelete = (ImageView) view.findViewById(R.id.image_delete);
            viewHolder.textDate = (TextView) view.findViewById(R.id.text_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textUserName.setText(commentArrayList.get(position).getUserFullName());
        viewHolder.editComment.setText(commentArrayList.get(position).getCommentText());

        String date=commentArrayList.get(position).getDate();
        viewHolder.textDate.setText(date);

        Picasso.with(context).load(commentArrayList.get(position).getUserImageURL())
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(viewHolder.imageUser);

        if((""+commentArrayList.get(position).getUserId()).equals(AppConstants.USER_ID) || (AppConstants.USER_ID).equals(FragmentFile.fileOwnerId))
            viewHolder.imageDelete.setVisibility(View.VISIBLE);
        else
            viewHolder.imageDelete.setVisibility(View.GONE);

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DELETE",commentArrayList.get(position).getCommentText());
                showDeleteDialog(commentArrayList.get(position).getCommentId()+"", commentArrayList.get(position).getFileId()+"",
                        commentArrayList.get(position).getCommentText(), commentArrayList.get(position).getTimeDate(), position);
            }
        });

        viewHolder.imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedAdapter.dialog.dismiss();
                replaceUserFragment(position);
            }
        });

        viewHolder.textUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedAdapter.dialog.dismiss();
                replaceUserFragment(position);
            }
        });

        return view;
    }

    private void replaceUserFragment(int position) {
        Fragment fragment=new FragmentUser();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_USER_ID,commentArrayList.get(position).getUserId()+"");
        bundle.putString(AppConstants.KEY_USER_NAME,commentArrayList.get(position).getUserFullName());
        fragment.setArguments(bundle);
        MainActivity.replaceFragment(fragment,((Activity) context).getFragmentManager(),(Activity) context,R.id.content_frame);
    }

    private void showDeleteDialog(final String commentId, final String fileId, final String commentText, final String timeDate, final int position) {
        new android.app.AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete Comment")
                .setMessage("Are you sure to delete this comment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteComment(commentId, fileId, AppConstants.USER_ID, commentText, timeDate,  position);
                    }
                    //dialog.dismiss();

                }).setNegativeButton("No", null).show();

    }

    private void deleteComment(String commentId, String fileId, String userId, String commentText, String timeDate, final int position) {
        String [] paramsArray = new String[]{"commentid","fileid","userid","commenttext","timedate","date"};
        String [] valuesArray = new String[]{commentId,fileId,userId,commentText,timeDate,timeDate};

        FormBody.Builder builder= WebServiceHandler.createBuilder(paramsArray,valuesArray);
        WebServiceHandler webServiceHandler = new WebServiceHandler(context);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("DELETE",response);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")){
                                //FragmentFile_.textComments.setText("Comments ("+(commentArrayList.size()-1)+")");
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),"Comment deleted successfully", Snackbar.LENGTH_LONG).show();
                                commentArrayList.remove(position);
                                notifyDataSetChanged();
                            }
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(AppConstants.URL_DELETE_COMMENT,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
