package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.PromotionPagerAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.RippleBackground;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.model.Store;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.parasme.swopinfo.activity.MainActivity.replaceFragment;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentPromotionPager extends Fragment {

    private AppCompatActivity mActivity;
    private ViewPager pagerPromotions;
    private ArrayList<Store.Promotion> promotionArrayList;
    private MenuItem itemSearch, itemHome;
    private CircleImageView imgBusinessLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_promotion_pager, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        countCheckin();

        promotionArrayList = new ArrayList<>();
        setArrayList();

        Log.e("PromotionSize",FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions().size()+"");
        //pagerPromotions.setAdapter(new PromotionPagerAdapter(appCompatActivity, promotionArrayList));
        pagerPromotions.setAdapter(new PromotionPagerAdapter(mActivity, FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions()));

        final RippleBackground rippleBackground=(RippleBackground)view.findViewById(R.id.rippleBackground);
        rippleBackground.startRippleAnimation();

        imgBusinessLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = pagerPromotions.getCurrentItem();
                Log.e("BHAI",FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions().get(pos).getImageURL());
                String companyId = FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions().get(pos).getCompanyId();
                if (!companyId.equals("")) {
                    FragmentCompany.companyId = Integer.parseInt(companyId);
                    Fragment fragment = new FragmentCompany();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isOwnCompany", false);
                    bundle.putInt(AppConstants.KEY_COMPANY_ID, Integer.parseInt(companyId));
                    fragment.setArguments(bundle);
                    replaceFragment(fragment, getFragmentManager(), mActivity, R.id.content_frame);
                }
            }
        });

        return view;
    }

    private void countCheckin() {
        String userId = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"";
        String retailerId = FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getRetailerId()+"";
        String storeId = FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getStoreId();

        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("COunt",response);
            }
        };
        try {
            webServiceHandler.get("https://swopinfo.com/checkincounts.aspx?user_id="+userId + "&retailerid_id="+retailerId+ "&storeid_id="+storeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setArrayList() {
        TypedArray gamePromos = getResources().obtainTypedArray(R.array.game);
        TypedArray hifiPromos = getResources().obtainTypedArray(R.array.hifi);
        TypedArray pickPromos = getResources().obtainTypedArray(R.array.pick);
        TypedArray sparPromos = getResources().obtainTypedArray(R.array.spar);
        TypedArray woolPromos = getResources().obtainTypedArray(R.array.wool);

        switch (FragmentRetailerLogos.retailerPosition){
            case 0:
                for (int i = 0; i <gamePromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(gamePromos.getResourceId(i,0)));
                }
                break;
            case 1:
                for (int i = 0; i <hifiPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(hifiPromos.getResourceId(i,0)));
                }
                break;
            case 2:
                for (int i = 0; i <pickPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(pickPromos.getResourceId(i,0)));
                }
                break;
            case 3:
                for (int i = 0; i <sparPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(sparPromos.getResourceId(i,0)));
                }
                break;
            case 4:
                for (int i = 0; i <woolPromos.length() ; i++) {
                    promotionArrayList.add(new Store.Promotion(woolPromos.getResourceId(i,0)));
                }
                break;
        }
    }


    private void findViews(View view) {
        pagerPromotions = (ViewPager) view.findViewById(R.id.view_pager_promotions);
        imgBusinessLink = (CircleImageView) view.findViewById(R.id.img_business);
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
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Promotions");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        itemSearch= menu.findItem(R.id.menu_search);
        MenuItem itemLive= menu.findItem(R.id.menu_live);
        itemLive.setVisible(false);

        itemHome = menu.findItem(R.id.menu_done);
        itemHome.setTitle("Share");
        itemSearch.setVisible(false);
        itemHome.setVisible(true);
        itemHome.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int pos = pagerPromotions.getCurrentItem();
/*
                String url = FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions().get(pos).getImageURL();
                String username = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME)+" " +
                        SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing Promo");
                i.putExtra(Intent.EXTRA_TEXT, username +" shared below promotion with you\n"+url+"\n\nClick below link if you want see more\nhttp://onelink.to/3f7rh3");
                startActivity(Intent.createChooser(i, "Share Promo"));
*/



                String url = FragmentHome.retailerList.get(FragmentRetailerLogos.retailerPosition).getPromotions().get(pos).getImageURL();
                String username = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME)+" " +
                        SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME);
                ImageView imageView = (ImageView) pagerPromotions.findViewWithTag(pos+"");
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, username +" shared below promotion with you\n"+url+"\n\nClick below link if you want see more\nhttp://onelink.to/3f7rh3");
                String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap, "", null);
                Uri screenshotUri = Uri.parse(path);

                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share Promotion..."));
                return false;
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        itemSearch.setVisible(true);
        itemHome.setVisible(false);
    }


    public void shareBitmapToApps(ImageView imageView) {

        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        i.putExtra(Intent.EXTRA_STREAM, getImageUri(mActivity, getBitmapFromView(imageView)));
        i.putExtra(android.content.Intent.EXTRA_TEXT, "If you would like to see more promotions like these. Click here.\n www.swopinfo.com\nor download the app\nhttp://play.google.com/store/apps/details?id=com.parasme.swopinfo");
        try {
            startActivity(Intent.createChooser(i, "Share Promotion"));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Promotion", null);
        return Uri.parse(path);
    }
}