package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.AllFilesRecyclerViewAdapter;
import com.parasme.swopinfo.adapter.CommentAdapter;
import com.parasme.swopinfo.adapter.MultipleFilesRecyclerViewAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.CustomGridLayoutManager;
import com.parasme.swopinfo.helper.CustomWebView;
import com.parasme.swopinfo.helper.NonScrollRecyclerView;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.Progress;
import com.parasme.swopinfo.webservice.ProgressListener;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okio.BufferedSink;
import okio.Okio;

import static com.parasme.swopinfo.helper.Utils.createFileURL;
import static com.parasme.swopinfo.helper.Utils.createThumbURL;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentAllFiles extends Fragment implements View.OnClickListener{

    private ArrayList<Upload> uploadArrayList = new ArrayList<>();
    private NonScrollRecyclerView recyclerView;
    private TextView textShared,textDisplayTime,textUserName;
    private ImageView imageUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_files, container, false);

        uploadArrayList = (ArrayList<Upload>) this.getArguments().getSerializable("uploadList");
        findViews(view);


        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        setData();

        return view;
    }

    private void setData() {
        String shareText = uploadArrayList.get(0).getCommentText();
        if(shareText.equals(""))
            shareText = uploadArrayList.get(0).getDescription();
        textShared.setText(shareText);
        textDisplayTime.setText(uploadArrayList.get(0).getDisplayTime());
        textUserName.setText(uploadArrayList.get(0).getUserFullName());

        loadUserOrCompanyThumb(uploadArrayList.get(0).getUserThumbURL(),imageUser);
    }

    private void findViews(View view) {
        textShared = (TextView) view.findViewById(R.id.textShared);
        textDisplayTime = (TextView) view.findViewById(R.id.textTime);
        textUserName = (TextView) view.findViewById(R.id.textUserName);
        imageUser = (ImageView) view.findViewById(R.id.imageUser);
        recyclerView = (NonScrollRecyclerView) view.findViewById(R.id.recycler_all_files);
        CustomGridLayoutManager staggeredGridLayoutManager;

        recyclerView.setHasFixedSize(false);


        staggeredGridLayoutManager = new CustomGridLayoutManager(1, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        AllFilesRecyclerViewAdapter rcAdapter = new AllFilesRecyclerViewAdapter(getActivity(), uploadArrayList);
        recyclerView.setAdapter(rcAdapter);

    }

    @Override
    public void onClick(View v) {

    }

    private void loadUserOrCompanyThumb(String userThumbUrl, ImageView imageView) {
        userThumbUrl=userThumbUrl.substring(1);
        userThumbUrl=AppConstants.URL_DOMAIN+userThumbUrl;


        Picasso.with(getActivity()).load(userThumbUrl)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(imageView);
    }

}