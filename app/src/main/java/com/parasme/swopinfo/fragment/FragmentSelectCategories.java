package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.WindowCallbackWrapper;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.parasme.swopinfo.adapter.SelectCategoryAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

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

public class FragmentSelectCategories extends Fragment {

    private AppCompatActivity mActivity;
    private String[] categories;
    private ListView listCategories;
    private ArrayList<Category> categoryArrayList;
    private MenuItem itemSearch, itemDone;
    private SelectCategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_categories, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        categoryArrayList = new ArrayList<>();
        categories = getResources().getStringArray(R.array.categories);

        setListAdapter();

        return view;
    }

    private void setListAdapter() {
        for (int i = 0; i < categories.length; i++) {
            String id = String.valueOf(i+1);
            Category category = new Category();
            category.setCategoryId(i+1);
            category.setCategoryName(categories[i]);
            category.setCategoryChecked(false);

            if(!FragmentFavourites.favCatIds.contains(id))
                categoryArrayList.add(category);
        }
        adapter = new SelectCategoryAdapter(mActivity, R.layout.row_select_category, categoryArrayList);
        listCategories.setAdapter(adapter);
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
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Categories");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        itemSearch= menu.findItem(R.id.menu_search);
        itemDone = menu.findItem(R.id.menu_done);
        itemSearch.setVisible(false);
        itemDone.setVisible(true);
        itemDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String ids = adapter.checkedCategoryIds();
                if(ids.equals(""))
                    replaceFavFragment();
                else
                    addToFav(ids, AppConstants.USER_ID);

                return false;
            }
        });
    }

    private void addToFav(final String ids, String userId) {
        String url = "http://dev.swopinfo.com/AddremoveFav.aspx?user_id="+userId+"&category_ids="+ids+"&action=add";
        WebServiceHandler webServiceHandle = new WebServiceHandler(mActivity);
        webServiceHandle.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Add",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optString("Response").equalsIgnoreCase("sucess")){
                                adapter.removeCategories(ids);
/*
                                for (int i = 0; i < categoryArrayList.size(); i++) {
                                    if(categoryArrayList.get(i).isCategoryChecked()) {
                                        Log.e("check",categoryArrayList.get(i).getCategoryName());
                                        FragmentFavourites.favCatIds.add(categoryArrayList.get(i).getCategoryId() + "");
                                        categoryArrayList.remove(i);
                                        adapter.notifyDataSetChanged();

                                    }
                                }
*/
                            }
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandle.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replaceFavFragment() {
        Fragment fragment = new FragmentFavourites();
        if(!SharedPreferenceUtility.getInstance().get(AppConstants.PREF_CHECK_IN_INTRO,false)) {
            SharedPreferenceUtility.getInstance().save(AppConstants.PREF_CHECK_IN_INTRO,true);
            Bundle bundle = new Bundle();
            bundle.putBoolean("FirstIntro", true);
            fragment.setArguments(bundle);
        }

        MainActivity.replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);

    }

    @Override
    public void onStop() {
        super.onStop();
        itemSearch.setVisible(true);
        itemDone.setVisible(false);
    }

}