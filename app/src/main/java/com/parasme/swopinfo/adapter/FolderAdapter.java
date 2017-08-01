package com.parasme.swopinfo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentUploadsWrapper;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Folder;
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

import static com.parasme.swopinfo.fragment.FragmentCompany.companyId;

/**
 * Created by SoNu on 6/6/2016.
 */

public class FolderAdapter extends ArrayAdapter<Upload> {

    private int resourceId;
    private ArrayList<Folder> folderArrayList;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater vi;
    private boolean isCompanyFolders;;
    public int selectedPosition = 0;

    public FolderAdapter(Context context, int resourceId, ArrayList<Folder> folderArrayList, boolean isCompanyFolders) {
        // TODO Auto-generated constructor stub
        super(context,resourceId);
        this.folderArrayList = folderArrayList;
        this.context = context;
        this.resourceId=resourceId;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isCompanyFolders = isCompanyFolders;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return folderArrayList.size();
    }


    class ViewHolder
    {
        TextView textFolderName;
        TextView textFiles;
        ImageView imageFolderThumb,imageDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=convertView;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = vi.inflate(resourceId, parent, false);
            viewHolder.textFolderName = (TextView) view.findViewById(R.id.text_folder_name);
            viewHolder.textFiles = (TextView) view.findViewById(R.id.text_files);
            viewHolder.imageFolderThumb = (ImageView) view.findViewById(R.id.image_folder_thumb);
            viewHolder.imageDelete = (ImageView) view.findViewById(R.id.imageDelete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final String folderName=folderArrayList.get(position).getFolderName();
        viewHolder.textFolderName.setText(folderName.equals("") ? "/" : folderName);
        viewHolder.textFiles.setText("Files: "+folderArrayList.get(position).getNumOfFiles());

        String thumbURL="";

        if(!isCompanyFolders)
            thumbURL = AppConstants.URL_DOMAIN+"upload/user"+folderArrayList.get(position).getUserId()+"/"+folderName+"/FolderThumbnail.jpg";
        else
            thumbURL = AppConstants.URL_DOMAIN+"upload/company"+folderArrayList.get(position).getUserId()+"/"+folderName+"/FolderThumbnail.jpg";

        thumbURL = thumbURL.replace(" ","%20");
        Log.e("aaa", "getView: "+thumbURL );

        loadFolderThumb(thumbURL);

        Fragment currentFragment = ((Activity)context).getFragmentManager().findFragmentById(R.id.content_frame);
        if(currentFragment instanceof FragmentUploadsWrapper)
            viewHolder.imageDelete.setVisibility(View.VISIBLE);
        else if(currentFragment instanceof FragmentCompany && companyId == (int)SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID))
            viewHolder.imageDelete.setVisibility(View.VISIBLE);
        else
            viewHolder.imageDelete.setVisibility(View.GONE);

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompanyFolders)
                    deleteAlertDialog(AppConstants.URL_COMAPNY + FragmentCompany.companyId + "/deletefolder/" + folderName,position);
                else
                    deleteAlertDialog(AppConstants.URL_USER + AppConstants.USER_ID + "/deletefolder/" + folderName,position);
            }
        });

        return view;
    }

    private void deleteAlertDialog(final String url,  final int position){
        AlertDialog.Builder adb = new AlertDialog.Builder((Activity)context);
        adb.setMessage("Are you sure to delete the folder?");
        adb.setTitle("Delete Folder");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isCompanyFolders)
                    deleteFolder(url, position);
                else
                    deleteFolder(url, position);
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();

    }

    private void deleteFolder(String url, final int position) {
        url = url.replace(" ","%20");
        WebServiceHandler webServiceHandler = new WebServiceHandler(((Activity)context));
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Del Folder", "onResponse: "+response );
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                folderArrayList.remove(position);
                                notifyDataSetChanged();
                            }
                            else
                                MyApplication.alertDialog((Activity)context, "Unable to delete the folder","Delete Folder");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.post(url,WebServiceHandler.createBuilder(new String[]{"userid"}, new String[]{AppConstants.USER_ID}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFolderThumb(final String thumbURL) {
        Picasso picasso = new Picasso.Builder(context)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("URI EXCP", uri.toString()+"___"+exception.toString() );
                        //Here your log
                    }
                })
                .build();
        picasso.load(thumbURL)
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .placeholder(R.drawable.ic_folder)
                .into(viewHolder.imageFolderThumb);

/*
        Picasso.with(context)
                .load(thumbURL)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(viewHolder.imageBookmarkIcon, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        String newURL = thumbURL.replace("/user","/company");
                        Log.e("FOLDERTHUMB", "second: "+newURL );
                        Picasso.with(context).load(newURL)
                                .error(R.drawable.ic_folder)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(viewHolder.imageBookmarkIcon);
                    }
                });
*/

    }

}
