package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
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
import com.parasme.swopinfo.activity.LocationActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FavouriteAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Category;
import com.parasme.swopinfo.model.Retailer;
import com.parasme.swopinfo.model.Store;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    private ArrayList<Category> favArrayList;
    private MenuItem itemSearch, itemGo, itemEdit;
    public static ArrayList<String> favCatIds;
    public static List<String> savedIdsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_categories, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        favArrayList = new ArrayList<>();
        favCatIds = new ArrayList<>();

        getFavoriteCategories(AppConstants.USER_ID);

        return view;
    }

    private void getFavoriteCategories(String userId){
        String [] categories = getResources().getStringArray(R.array.categories);
        TypedArray categoryIcons = getResources().obtainTypedArray(R.array.category_icons);

        String ids = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_FAV_IDS,"");
        Log.e("SAVEDIDS",ids);
        savedIdsList = new LinkedList<String>(Arrays.asList(ids.split(",")));

        for (int i = 0; i < categories.length; i++) {
            String id = String.valueOf(i+1);
            Category category = new Category();
            category.setCategoryId(i+1);
            category.setCategoryIcon(categoryIcons.getResourceId(i,1));
            category.setCategoryName(categories[i]);
            category.setCategoryChecked(false);
            if(savedIdsList.contains(id))
                favArrayList.add(category);
        }

        if(favArrayList.size()>0)
            listCategories.setAdapter(new FavouriteAdapter(mActivity, R.layout.row_favourites, favArrayList));
        else
            MyApplication.alertDialog(mActivity,"You have not added any favorites","Favorites");

    }

/*
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
                                    favArrayList.add(category);
                                    favCatIds.add(jsonObject1.optInt("categoryid")+"");
                                }
                                listCategories.setAdapter(new FavouriteAdapter(mActivity, R.layout.row_favourites, favArrayList));
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
*/



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
        MenuItem itemLive= menu.findItem(R.id.menu_live);
        itemLive.setVisible(false);
        itemGo = menu.findItem(R.id.menu_done);
        itemEdit = menu.findItem(R.id.menu_edit);
        itemGo.setTitle("Go");

        itemSearch.setVisible(false);
        itemGo.setVisible(true);
        itemEdit.setVisible(true);
        itemGo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
/*
                if(FragmentHome.retailerList.size()!=0)
                    MainActivity.replaceFragment(new FragmentRetailerLogos(), getFragmentManager(), mActivity, R.id.content_frame);
                else
                    checkIn(LocationActivity.mCurrentLocation.getLatitude()+"",LocationActivity.mCurrentLocation.getLongitude()+"", AppConstants.USER_ID);
*/
                MainActivity.replaceFragment(new FragmentRetailerLogos(), getFragmentManager(), mActivity, R.id.content_frame);


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

    private void checkIn(String latitude, String longitude, String userId) {
        String catIds = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_FAV_IDS);
        Log.e("catids", catIds);
        String url = "http://dev.swopinfo.com/retailerswithpromo.aspx?cat_id="+catIds+"&retailer_lat="+latitude+
                "&retailer_long="+longitude;

        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Checkin",response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.opt("Result") instanceof JSONArray){
                                JSONArray jsonArray = jsonObject.optJSONArray("Result");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject retailerObject = jsonArray.getJSONObject(i);
                                    Retailer retailer = new Retailer();
                                    retailer.setRetailerLogo(retailerObject.optString("storelogo"));

                                    ArrayList<Store.Promotion> promotionList = new ArrayList<>();

                                    JSONArray promotionArray = retailerObject.optJSONArray("pro");
                                    for (int j = 0; j < promotionArray.length(); j++) {
                                        JSONObject promotionObject = promotionArray.optJSONObject(j);
                                        Store.Promotion promotion = new Store.Promotion();
                                        promotion.setImageURL(promotionObject.optString("promotionimg"));
                                        promotionList.add(promotion);
                                    }

                                    retailer.setPromotions(promotionList);
                                    if(promotionArray.length()!=0)
                                        FragmentHome.retailerList.add(retailer);
                                }

                                ((MainActivity)mActivity).replaceFragment(new FragmentRetailerLogos(),getFragmentManager(),mActivity,R.id.content_frame);

                            }
                            else
                                MyApplication.alertDialog(mActivity,"No Store found near you", "Check In");
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

}