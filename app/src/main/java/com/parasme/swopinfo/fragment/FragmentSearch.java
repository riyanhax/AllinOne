package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.SearchAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.ExpandAnimation;
import com.parasme.swopinfo.helper.NestedListView;
import com.parasme.swopinfo.model.Follow;
import com.parasme.swopinfo.model.Group;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.parasme.swopinfo.helper.Utils.createFileURL;
import static com.parasme.swopinfo.helper.Utils.createThumbURL;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentSearch extends Fragment{

    private ArrayList<Follow> userArrayList = new ArrayList<>();
    private ArrayList<Upload> uploadArrayList = new ArrayList<>();
    private ArrayList<Group> groupArrayList = new ArrayList<>();
    private AppCompatActivity mActivity;
    private NestedListView listUsers;
    private NestedListView listUploads;
    private NestedListView listGroups;
    private TextView textHeaderUsers;
    private TextView textHeaderUploads;
    private TextView textHeaderGroups;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Resetting static data for basefragment to load current user's following, followers and groups
        BaseFragment.isGroupsLoaded = false;
        BaseFragment.isFollowersLoaded = false;
        BaseFragment.isFollowingLoaded = false;

        listUsers = (NestedListView) view.findViewById(R.id.listUsers);
        listUploads = (NestedListView) view.findViewById(R.id.listUploads);
        listGroups = (NestedListView) view.findViewById(R.id.listGroups);
        textHeaderUsers = (TextView) view.findViewById(R.id.textHeaderUsers);
        textHeaderUploads = (TextView) view.findViewById(R.id.textHeaderUploads);
        textHeaderGroups = (TextView) view.findViewById(R.id.textHeaderGroups);

        textHeaderUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ExpandAnimation expandAni = new ExpandAnimation(listUsers, 500);

                // Start the animation on the toolbar
                listUsers.startAnimation(expandAni);
            }
        });

        textHeaderUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandAnimation expandAni = new ExpandAnimation(listUploads, 500);

                // Start the animation on the toolbar
                listUploads.startAnimation(expandAni);
            }
        });

        textHeaderGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandAnimation expandAni = new ExpandAnimation(listGroups, 500);

                // Start the animation on the toolbar
                listGroups.startAnimation(expandAni);
            }
        });

        String query = this.getArguments().getString("query");
        getSearchResults(query);

        listUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putInt("fildeId",uploadArrayList.get(i).getFileId());
                Fragment fragment = new FragmentFile();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
                MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
            }
        });

        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Fragment fragment = new FragmentUser();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID,userArrayList.get(position).getUserId()+"");
                bundle.putString(AppConstants.KEY_USER_NAME,userArrayList.get(position).getFullName());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).addToBackStack(null).commit();
//                MainActivity.replaceFragment(fragment, appCompatActivity.getFragmentManager(),appCompatActivity ,R.id.content_frame);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Search Result");
    }

    public void getSearchResults(String queryString){
        userArrayList.clear();
        uploadArrayList.clear();
        groupArrayList.clear();
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("SEARCH",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray companyGroupArray = jsonObject.optJSONArray("companiesgroups");
                    JSONArray fileResults = jsonObject.optJSONArray("fileresults");
                    JSONArray userResults = jsonObject.optJSONArray("userresults");

                    // Getting user results
                    for(int i=0;i<userResults.length();i++){
                        JSONObject jsonObject1 = userResults.optJSONObject(i);
                        Follow follow = new Follow();
                        follow.setUserId(jsonObject1.optInt("userid"));
                        follow.setFullName(EmojiHandler.decodeJava(jsonObject1.optString("UserFullName")));

                        userArrayList.add(follow);
                    }

                    // Getting upload results
                    for(int i=0;i<fileResults.length();i++) {
                        JSONObject jsonObject1 = fileResults.optJSONObject(i);
                        Upload upload = new Upload();
                        upload.setOwnerId(jsonObject1.optInt("userid"));
                        upload.setUserId(jsonObject1.optInt("userid"));
                        upload.setFileId(jsonObject1.optInt("fileid"));
                        upload.setViewsCount(jsonObject1.optInt("viewscount"));
                        upload.setDownloadsCount(jsonObject1.optInt("downloadscount"));
                        upload.setCompanyId(jsonObject1.optInt("companyid"));
                        upload.setStatusInfoId(jsonObject1.optInt("statusinformationid"));

                        upload.setRealFileName(jsonObject1.optString("realfilename"));
                        upload.setFileName(jsonObject1.optString("filename"));
                        upload.setFileType(jsonObject1.optString("filetype"));
                        upload.setFileSize(jsonObject1.optString("filesize"));
                        upload.setTimeDate(jsonObject1.optString("timedate"));
                        upload.setCategory(jsonObject1.optString("category"));
                        upload.setTitle(jsonObject1.optString("title"));
                        upload.setTags(jsonObject1.optString("tags"));
                        upload.setDescription(jsonObject1.optString("description"));
                        upload.setVeryPdf(jsonObject1.optString("verypdf"));

                        String folderName = jsonObject1.optString("foldername");
                        upload.setFolderName(folderName);

//                        String thumbName = jsonObject1.optString("Thumbnailfilename");
//                        upload.setThumbURL(createThumbURL(jsonObject1.optInt("userid")+"",folderName, thumbName, jsonObject1.optString("filetype"), jsonObject1.optInt("companyid")));


                        String thumbName = jsonObject1.optString("Thumbnailfilename");
                        String videoURL = jsonObject1.optString("videourl");
                        if(jsonObject1.optString("filetype").equalsIgnoreCase("videourl")  && videoURL.contains("youtube"))
                        {
                            String videoId="";
                            if(videoURL.contains("v="))
                                videoId = videoURL.split("=")[1];
                            else if(videoURL.contains("embed"))
                                videoId = videoURL.substring(videoURL.lastIndexOf("/")+1,videoURL.length());
                            Log.e("vvvvv", "run: "+videoId );
                            upload.setThumbURL("http://img.youtube.com/vi/"+videoId+"/default.jpg");
                        }
                        else
                            upload.setThumbURL(createThumbURL(jsonObject1.optInt("userid")+"",folderName, thumbName, jsonObject1.optString("filetype"), jsonObject1.optInt("companyid")));

                        upload.setVideoURL(videoURL);







                        upload.setBroadcast(jsonObject1.optBoolean("broadcast"));
                        upload.setComments(jsonObject1.optBoolean("comments"));
                        upload.setRatings(jsonObject1.optBoolean("ratings"));
                        upload.setEmbedding(jsonObject1.optBoolean("embedding"));
                        upload.setDownloads(jsonObject1.optBoolean("downloads"));
                        upload.setProfileRemoved(jsonObject1.optBoolean("profileremoved"));
                        upload.setCompanyUploaded(jsonObject1.optBoolean("companyuploaded"));
                        upload.setIncludeExtension(jsonObject1.optBoolean("IncludesExtension"));
                        upload.setFileURL(createFileURL(jsonObject1));

                        uploadArrayList.add(upload);

                    }


                    // Getting groups
                    for(int i=0;i<companyGroupArray.length();i++){
                        JSONObject jsonObject1 = companyGroupArray.optJSONObject(i);
                        Group group = new Group();
                        group.setGroupId(jsonObject1.optInt("id"));
                        group.setGroupName(EmojiHandler.decodeJava(jsonObject1.optString("name")));
                        group.setExtraInfo1(jsonObject1.optString("ExtraInfo1"));
                        group.setExtraInfo2(jsonObject1.optString("ExtraInfo2"));
                        group.setBusinessCity(jsonObject1.optString("BussinessCity"));
                        group.setType(jsonObject1.optString("type"));

                        groupArrayList.add(group);
                    }

                }catch (JSONException e){}

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textHeaderUsers.setText("Users ("+userArrayList.size()+")");
                        textHeaderUploads.setText("Files ("+uploadArrayList.size()+")");
                        textHeaderGroups.setText("Groups ("+groupArrayList.size()+")");

                        listUsers.setAdapter(new SearchAdapter(mActivity,R.layout.row_search,userArrayList,uploadArrayList,groupArrayList,0));
                        listUploads.setAdapter(new SearchAdapter(mActivity,R.layout.row_search,userArrayList,uploadArrayList,groupArrayList,1));
                        listGroups.setAdapter(new SearchAdapter(mActivity,R.layout.row_search,userArrayList,uploadArrayList,groupArrayList,2));

//                        ExpandAnimation expandAni = new ExpandAnimation(listUsers, 500);
//                        listUsers.startAnimation(expandAni);

                    }
                });
            }
        };
        try {
            webServiceHandler.get(AppConstants.URL_DOMAIN+"api/Search/"+queryString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager =
                (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(mActivity.getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>0){
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setIconified(true);
                    getSearchResults(query);}
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity){
            mActivity=(AppCompatActivity) activity;
        }
    }

}