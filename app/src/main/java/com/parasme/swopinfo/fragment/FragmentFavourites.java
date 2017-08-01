package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FavouriteAdapter;
import com.parasme.swopinfo.adapter.SelectCategoryAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentFavourites extends Fragment {

    private AppCompatActivity mActivity;
    private String[] categories;
    private ListView listCategories;
    private ArrayList<Category> categoryArrayList;
    private MenuItem itemSearch, itemGo, itemEdit;
    public static ArrayList<String> favCatIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_categories, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        categoryArrayList = new ArrayList<>();
        favCatIds = new ArrayList<>();

        getFavoriteCategories(AppConstants.USER_ID);

        return view;
    }

    private void getFavoriteCategories(String userId) {
        String url = "http://dev.swopinfo.com/Userfav.aspx?user_id="+userId;
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Favs",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.opt("Data") instanceof JSONArray){
                                JSONArray jsonArray = jsonObject.optJSONArray("Data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                    Category category = new Category();
                                    category.setCategoryId(jsonObject1.optInt("categoryid"));
                                    category.setCategoryName(jsonObject1.optString("categoryname"));
                                    categoryArrayList.add(category);
                                    favCatIds.add(jsonObject1.optInt("categoryid")+"");
                                }
                                listCategories.setAdapter(new FavouriteAdapter(mActivity, R.layout.row_favourites, categoryArrayList));
                            }
                            else
                                MyApplication.alertDialog(mActivity,"You have not added any favorites","Favorites");
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void findViews(View view) {
        listCategories = (ListView) view.findViewById(R.id.list_categories);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity) {
            mActivity = (AppCompatActivity) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Favourites");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        itemSearch= menu.findItem(R.id.menu_search);
        itemGo = menu.findItem(R.id.menu_done);
        itemEdit = menu.findItem(R.id.menu_edit);
        itemGo.setTitle("Go");

        itemSearch.setVisible(false);
        itemGo.setVisible(FragmentHome.storeId == 0 ? false : true);
        itemEdit.setVisible(true);
        itemGo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity.replaceFragment(new FragmentPromotions(), getFragmentManager(), mActivity, R.id.content_frame);
                return false;
            }
        });
        itemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity.replaceFragment(new FragmentSelectCategories(), getFragmentManager(), mActivity, R.id.content_frame);
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        itemSearch.setVisible(true);
        itemGo.setVisible(false);
        itemEdit.setVisible(false);
    }
}