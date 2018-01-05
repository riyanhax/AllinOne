package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;

/**
 * Created by SoNu on 6/6/2016.
 */

public class BookmarkAdapter extends ArrayAdapter<Upload> {

    private int resourceId;
    private ArrayList<Upload> bookmarkArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;

    public BookmarkAdapter(Context context, int resourceId, ArrayList<Upload> bookmarkArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.bookmarkArrayList = bookmarkArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bookmarkArrayList.size();
    }


    class ViewHolder
    {
        TextView textBookmarkTitle;
        TextView textBookmarkURL;
        ImageView imageBookmarkIcon;
        ImageView imageDelete;
        CircleImageView imgShare;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textBookmarkTitle = (TextView) view.findViewById(R.id.textBookmarkTitle);
            viewHolder.textBookmarkURL = (TextView) view.findViewById(R.id.textBookmarkURL);
            viewHolder.imageBookmarkIcon = (ImageView) view.findViewById(R.id.imageFavIcon);
            viewHolder.imageDelete = (ImageView) view.findViewById(R.id.imageDelete);
            viewHolder.imgShare = (CircleImageView) view.findViewById(R.id.imgShare);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textBookmarkTitle.setText(bookmarkArrayList.get(position).getTitle());
        viewHolder.textBookmarkURL.setText(bookmarkArrayList.get(position).getRealFileName());
        Picasso.with(context).load(bookmarkArrayList.get(position).getThumbURL()).placeholder(R.drawable.ic_favicon).error(R.drawable.ic_favicon).into(viewHolder.imageBookmarkIcon);


        viewHolder.textBookmarkURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String url = bookmarkArrayList.get(position).getRealFileName();
                    Log.e("URL", "onClick: "+url );
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);

                }catch (ActivityNotFoundException e){
                    MyApplication.alertDialog((Activity) context, "No app found to open the bookmark","Bookmarks");
                }
            }
        });

        viewHolder.textBookmarkTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String url = bookmarkArrayList.get(position).getRealFileName();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);

                }catch (ActivityNotFoundException e){
                    MyApplication.alertDialog((Activity) context, "No app found to open the bookmark","Bookmarks");
                }
            }
        });

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(bookmarkArrayList.get(position).getFileId()+"", position);
            }
        });

       final String bookmarkUrlToShare = bookmarkArrayList.get(position).getRealFileName();
        viewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.shareURLCustomIntent(bookmarkUrlToShare, (Activity) context);
            }
        });

        return view;
    }


    private void showDeleteDialog(final String fileId, final int position) {
        new android.app.AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete Bookmark")
                .setMessage("Are you sure you want delete this bookmark?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteBookmark(fileId, AppConstants.USER_ID,position);
                    }
                    //dialog.dismiss();

                }).setNegativeButton("No", null).show();

    }

    private void deleteBookmark(String fileId, String userId, final int position) {

        FormBody.Builder builder= WebServiceHandler.createBuilder(new String[]{"test"},new String[]{"test"});
        WebServiceHandler webServiceHandler = new WebServiceHandler(context);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("DELETE RESP",response);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.optString("Code").equals("200")){
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),"Bookmark deleted successfully", Snackbar.LENGTH_LONG).show();
                                bookmarkArrayList.remove(position);
                                notifyDataSetChanged();
                            }
                            else
                                MyApplication.alertDialog((Activity) context,"Could not delete bookmark","Bookmark");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            // Differentiating delete url on basis of company id
             webServiceHandler.post(AppConstants.URL_DOMAIN+"api/file/"+fileId+"/delete/"+userId,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
