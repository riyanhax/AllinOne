package com.parasme.swopinfo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LoginActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by :- Mukesh Kumawat on 10-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentTest extends Fragment{

    private View baseView;
    private AppCompatActivity mActivity;
    private WebView webView;
    private ProgressBar progressBar;

    private static String PROVIDER="gps";
    private LocationManager myLocationManager=null;

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_subscription, container, false);
        webView = (WebView) baseView.findViewById(R.id.webView);


        myLocationManager=(LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
        webView.addJavascriptInterface(new Locater(), "locater");

        progressBar = (ProgressBar) baseView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://appone.biz/test/geo.html");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", "onPageFinished: "+url );
                if(url.equals(AppConstants.URL_DOMAIN+"login.cshtml")) {
                    SharedPreferenceUtility.getInstance().clearSharedPreferences();
                    // replaceFragment(new FragmentHome_(),getFragmentManager());
                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }

                view.loadUrl(url);
                return false;
            }
        });

//        http://dev.swopinfo.com/order_return.aspx?56dd46a7-8c18-4707-8751-484d7b51be61
//        http://dev.swopinfo.com/login.cshtml
        return baseView;
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

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        myLocationManager.requestLocationUpdates(PROVIDER, 0,
                0,
                onLocation);
    }

    @Override
    public void onPause() {
        super.onPause();
        myLocationManager.removeUpdates(onLocation);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)mActivity).getSupportActionBar().show();
    }


    LocationListener onLocation=new LocationListener() {
        public void onLocationChanged(Location location) {
            StringBuilder buf=new StringBuilder("javascript:whereami('");

            buf.append("a"+String.valueOf(location.getLatitude()));
            buf.append("','");
            buf.append("a"+String.valueOf(location.getLongitude()));
            buf.append("')");

            Log.e("URL",buf.toString());
            webView.loadUrl(buf.toString());
        }

        public void onProviderDisabled(String provider) {
            // required for interface, not used
        }

        public void onProviderEnabled(String provider) {
            // required for interface, not used
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // required for interface, not used
        }
    };


    public class Locater {
        public String getLocation() throws JSONException {
            Location loc=myLocationManager.getLastKnownLocation(PROVIDER);

            if (loc==null) {
                return(null);
            }

            JSONObject json=new JSONObject();

            json.put("lat", loc.getLatitude()+"");
            json.put("lon", loc.getLongitude()+"");

            return(json.toString());
        }
    }
}