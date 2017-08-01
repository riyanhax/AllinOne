package com.parasme.swopinfo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by :- Mukesh Kumawat on 02-Feb-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

@EActivity(R.layout.fragment_subscription)
public class SubscriptionActivity extends AppCompatActivity {

    @ViewById WebView webView;
    @ViewById ProgressBar progressBar;

    @AfterViews
    protected void init(){
        progressBar.setVisibility(View.VISIBLE);
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
                    startActivity(new Intent(SubscriptionActivity.this, LoginActivity_.class));
                    finish();
                }

                view.loadUrl(url);
                return false;
            }
        });

    }
}
