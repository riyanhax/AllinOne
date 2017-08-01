package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentHome;
import com.parasme.swopinfo.fragment.FragmentUploads;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

import static com.parasme.swopinfo.fragment.FragmentUploads.adapter;
import static com.parasme.swopinfo.helper.Utils.getViewByPosition;

/**
 * Created by SoNu on 6/6/2016.
 */

public class UploadAdapter extends ArrayAdapter<Upload> {

    private int resourceId;
    private ArrayList<Upload> uploadArrayList;
    private Context context;
    private ViewHolder viewHolder;
    LayoutInflater vi;
    private SparseBooleanArray mSelectedItemsIds;

    public UploadAdapter(Context context, int resourceId, ArrayList<Upload> uploadArrayList) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.uploadArrayList = uploadArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return uploadArrayList.size();
    }



    class ViewHolder
    {
        ImageView imageUploadThumb,imageDelete;
        TextView textFileTitle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);

            viewHolder.imageUploadThumb = (ImageView) view.findViewById(R.id.image_upload_thumb);
            viewHolder.imageDelete = (ImageView) view.findViewById(R.id.image_delete);
            viewHolder.textFileTitle = (TextView) view.findViewById(R.id.text_file_title);


            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textFileTitle.setText(uploadArrayList.get(position).getTitle());

        // If file is not uploaded via company
        if(uploadArrayList.get(position).getCompanyId()==0) {
            if (AppConstants.USER_ID.equals(uploadArrayList.get(position).getUserId() + "") || AppConstants.USER_ID.equals(uploadArrayList.get(position).getOwnerId() + ""))
                viewHolder.imageDelete.setVisibility(View.VISIBLE);
            else
                viewHolder.imageDelete.setVisibility(View.GONE);
        }

        // When file is uploaded via company
        else {
            if (AppConstants.USER_ID.equals(uploadArrayList.get(position).getOwnerId() + ""))
                viewHolder.imageDelete.setVisibility(View.VISIBLE);
            else
                viewHolder.imageDelete.setVisibility(View.GONE);
        }

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DELETE",uploadArrayList.get(position).getFileId()+"");
                showDeleteDialog(uploadArrayList.get(position).getFileId()+"",position,uploadArrayList.get(position).getCompanyId());
            }
        });

        //Log.e("imgurl",uploadArrayList.get(position).getThumbURL());
        Picasso.with(context).load(uploadArrayList.get(position).getThumbURL()).placeholder(R.drawable.document_gray).error(R.drawable.document_gray).into(viewHolder.imageUploadThumb);

        return view;
    }

    private void showDeleteDialog(final String fileId, final int position, final int companyId) {
        new android.app.AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete File")
                .setMessage("Are you sure you want delete this file?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteFile(fileId,AppConstants.USER_ID,position,companyId);
                    }
                    //dialog.dismiss();

                }).setNegativeButton("No", null).show();

    }

    private void deleteFile(String fileId, String userId, final int position, int companyId) {

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
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),"File deleted successfully", Snackbar.LENGTH_LONG).show();
                                uploadArrayList.remove(position);
                                new FragmentHome().getFeeds((Activity) context, FragmentHome.listFeeds,FragmentHome.swipeRefreshLayout);

                                notifyDataSetChanged();
                            }
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            // Differentiating delete url on basis of company id
            if(companyId==0)
                webServiceHandler.post(AppConstants.URL_DOMAIN+"api/file/"+fileId+"/delete/"+userId,builder);
            else
                webServiceHandler.post(AppConstants.URL_DOMAIN+"api/file/"+fileId+"/deletecompanyfile/"+companyId,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Upload object) {
        // TODO Auto-generated method stub
        super.remove(object);
    }

    public void toggleSelection(int position, LinearLayout linearLayout) {
        selectView(position, !mSelectedItemsIds.get(position),linearLayout);
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value, LinearLayout linearLayout) {
        if (value){
            mSelectedItemsIds.put(position, value);
            linearLayout.setBackgroundResource(R.color.colorPrimary);
        }
        else{
            mSelectedItemsIds.delete(position);
            linearLayout.setBackgroundResource(android.R.color.transparent);
        }
        notifyDataSetChanged();

    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public ArrayList<String> getSelectedFileIds(GridView gridView) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        SparseBooleanArray selected = getSelectedIds();
        for (int i = 0; i < selected.size(); i++) {
            Log.e("TTTTTTTTTT", "onActionItemClicked: "+uploadArrayList.get(selected.keyAt(i)).getTitle() );
            stringArrayList.add(uploadArrayList.get(selected.keyAt(i)).getFileId()+"");
            LinearLayout linearLayout = (LinearLayout) getViewByPosition(selected.keyAt(i),gridView);
            linearLayout.setBackgroundResource(android.R.color.transparent);
        }
        return stringArrayList;
    }

}
