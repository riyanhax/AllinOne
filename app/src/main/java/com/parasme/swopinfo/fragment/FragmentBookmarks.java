package com.parasme.swopinfo.fragment;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.BookmarkAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentBookmarks extends BaseFragment implements Utils.AsyncResponse {

    private View childView;
    private ListView listBookmarks;
    private TextView emptyListText;
    public static ArrayList<Upload> bookmarksArrayList =new ArrayList<>();
    private Button btnAddBookmark;
    private EditText editBookmarkTitle;
    private BookmarkAdapter bookmarkAdapter;

    @Override
    public void onResume() {
        super.onResume();

        imageActionBookmarks.setImageResource(R.drawable.ic_bookmarks_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Bookmarks");
        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_bookmarks, null);

        listBookmarks = (ListView)  childView.findViewById(R.id.listBookmarks);
        emptyListText = (TextView) childView.findViewById(R.id.emptyListText);
        btnAddBookmark = (Button) childView.findViewById(R.id.btnAddBookmark);

        LinearLayout layout = (LinearLayout) baseView.findViewById(R.id.layout);
        //layout.setGravity(Gravity.CENTER);
        layout.addView(childView);

        String userId=AppConstants.USER_ID;
        String url=AppConstants.URL_USER + userId +"/bookmarks";

        try {
            getBookmarks(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        btnAddBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareBookmarkDialog();
            }
        });
    }

    private void prepareBookmarkDialog() {
        final Dialog dialog = Utils.loadBookmarkDialog(mActivity);
        final Button btnFetchTitle = (Button) dialog.findViewById(R.id.btnFetchTitle);
        final Button btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
        final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final EditText editBookmarkURL = (EditText) dialog.findViewById(R.id.editBookmarkURL);
        editBookmarkTitle = (EditText) dialog.findViewById(R.id.editBookmarkTitle);

        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnFetchTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookmarkURL = editBookmarkURL.getText().toString();
                if(bookmarkURL.equals("") || !bookmarkURL.contains(".")){
                    editBookmarkURL.setError("Please enter a correct URL first");
                    editBookmarkURL.requestFocus();
                    return;
                }
                new Utils(mActivity).getMetaDataFromURL(FragmentBookmarks.this,bookmarkURL);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookmarkURL = editBookmarkURL.getText().toString();
                String bookmarkTitle = editBookmarkTitle.getText().toString();
                if(bookmarkURL.equals("") || !bookmarkURL.contains(".")){
                    editBookmarkURL.setError("Please enter correct URL first");
                    editBookmarkURL.requestFocus();
                    return;
                }

                if(bookmarkTitle.equals("")){
                    editBookmarkTitle.setError("Please fetch title or enter title first");
                    editBookmarkTitle.requestFocus();
                    return;
                }

                addBookmark(AppConstants.URL_USER + AppConstants.USER_ID + "/addbookmark",bookmarkTitle, bookmarkURL,dialog);
            }
        });
    }

    private void addBookmark(String url, final String bookmarkTitle, final String bookmarkURL, final Dialog dialog) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("ADDBOOK", "onResponse: "+response );
                dialog.dismiss();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            if(new JSONObject(response).optString("Code").equals("200")){
                                Upload upload = new Upload();
                                upload.setFileId(0);
                                upload.setTitle(bookmarkTitle);
                                upload.setRealFileName(bookmarkURL.contains("http") ? bookmarkURL : "http://"+bookmarkURL);
                                upload.setThumbURL("https://www.google.com/s2/favicons?domain="+bookmarkURL);
                                bookmarksArrayList.add(upload);
                                bookmarkAdapter.notifyDataSetChanged();
                            }
                            else
                                MyApplication.alertDialog(mActivity,"Could not add bookmark","Bookmark");
                        }
                        catch (JSONException e){e.toString();}

                    }
                });



            }
        };

        try {
            webServiceHandler.post(url,WebServiceHandler.createBuilder(new String[]{"title","bookmarkurl"},new String[]{bookmarkTitle, bookmarkURL}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBookmarks(String url) throws IOException {
        bookmarksArrayList.clear();
        WebServiceHandler webServiceHandler =  new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener=new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.e("Bookmark Resp",response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        Upload bookmark = new Upload();
                        bookmark.setFileId(jsonObject.optInt("fileid"));
                        bookmark.setRealFileName(jsonObject.optString("realfilename"));
                        bookmark.setTitle(jsonObject.optString("title"));
                        bookmark.setUserId(jsonObject.optInt("userid"));
                        bookmark.setThumbURL("https://www.google.com/s2/favicons?domain="+jsonObject.optString("realfilename"));
                        bookmarksArrayList.add(bookmark);
                    }

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bookmarkAdapter = new BookmarkAdapter(mActivity,R.layout.row_bookmark, bookmarksArrayList);
                            listBookmarks.setAdapter(bookmarkAdapter);
                            listBookmarks.setEmptyView(emptyListText);
                        }
                    });
                }catch (JSONException e){}
            }
        };
        webServiceHandler.get(url);
    }

    @Override
    public void processFinish(String output) {
        editBookmarkTitle.setText(output.split(",")[0]);
    }
}