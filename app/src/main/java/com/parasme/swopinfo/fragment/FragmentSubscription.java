package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LoginActivity_;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;

/**
 * Created by :- Mukesh Kumawat on 10-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentSubscription extends Fragment{

    private View baseView;
    private AppCompatActivity mActivity;
    private WebView webView;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_subscription, container, false);

        progressBar = (ProgressBar) baseView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webView = (WebView) baseView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getResources().getString(R.string.subscription_url));
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
                    mActivity.startActivity(new Intent(mActivity, LoginActivity_.class));
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
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)mActivity).getSupportActionBar().show();
    }
}