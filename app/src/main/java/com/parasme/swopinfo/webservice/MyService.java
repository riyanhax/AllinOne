package com.parasme.swopinfo.webservice;

/**
 * Created by mukesh on 21/11/17.
 */

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LocationActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentFavourites;
import com.parasme.swopinfo.fragment.FragmentHome;
import com.parasme.swopinfo.fragment.FragmentPromotionPager;
import com.parasme.swopinfo.fragment.FragmentRetailerLogos;
import com.parasme.swopinfo.fragment.FragmentSelectCategories;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.model.Retailer;
import com.parasme.swopinfo.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Belal on 12/30/2016.
 */

public class MyService extends Service {

    final Activity activity = MainActivity.activityContext;
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // starting action of service
        startingHandlerForCheckIn();

        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    private void startingHandlerForCheckIn() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    Log.e("MyService","Service Triggered");
                    if (!(activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentRetailerLogos)
                            && !(activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentFavourites)
                            && !(activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentSelectCategories)
                            && !(activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentPromotionPager))
                                //checkIn(LocationActivity.mCurrentLocation.getLatitude()+"",LocationActivity.mCurrentLocation.getLongitude()+"", AppConstants.USER_ID);
                                checkIn("-26.195246","28.034088", AppConstants.USER_ID);
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 5000);
                }
            }
        };
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the action of service
    }

    private void checkIn(String latitude, String longitude, String userId) {
        FragmentHome.retailerList = new ArrayList<>();

        String catIds = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_FAV_IDS);
        Log.e("catids", catIds);
        String url = "https://swopinfo.com/retailerswithpromo.aspx?cat_id="+catIds+"&retailer_lat="+latitude+
                "&retailer_long="+longitude;

        WebServiceHandler webServiceHandler = new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Checkin",response);
                activity.runOnUiThread(new Runnable() {
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
                                    retailer.setStoreId(retailerObject.optString("storeID"));
                                    retailer.setRetailerMessage(retailerObject.optString("msg"));
                                    retailer.setRetailerId(retailerObject.optString("retailerid"));
                                    retailer.setRetailerName(retailerObject.optString("storename"));

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

                                Retailer retailer = new Retailer();
                                retailer.setStoreId("0");
                                retailer.setRetailerLogo("https://www.ecigssa.co.za/data/attachments/99/99598-ff08bbcb3dbbe9846619354ee13b48d2.jpg");
                                FragmentHome.retailerList.add(retailer);

                                ((MainActivity) activity).replaceFragment(new FragmentRetailerLogos(), activity.getFragmentManager(),activity, R.id.content_frame);

                            }
//                            else
//                                alertDialog(activity,"Thanks for Checking in. We unfortunately do not have stores listed in this Geolocated area but will have soon. Please ask your Local Retailers to join so that you can benefit.", "Check In");
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