package com.parasme.swopinfo.fragment;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.icu.util.IndianCalendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.helper.Indicator;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by SoNu on 8/31/2017.
 */

public class FragmentRadio extends Fragment {

    private ImageView imageDisc;
    public static WebView webView;
    private boolean isErrorFound = false;
    private RelativeLayout layout;
    private ProgressBar progressBar;
    private Button btnBack;
    private Button btnBackgroundPlay;
    private boolean isBgPlayEnabled;
    private TextView textClock;
    private Indicator indicator;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    private boolean mActive;
    private final Handler mHandler;

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            while(mActive){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (textClock != null) {
                            textClock.setText(getTime());
                        }
                    }
                });

            }
        }
    };

    public FragmentRadio() {
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);
        imageDisc = (ImageView) view.findViewById(R.id.image_disc);

        if (webView==null)
            webView = (WebView) view.findViewById(R.id.webView);

        layout = (RelativeLayout) view.findViewById(R.id.layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnBackgroundPlay = (Button) view.findViewById(R.id.btnBackgroundBack);
        textClock = (TextView) view.findViewById(R.id.text_clock);
        indicator = (Indicator) view.findViewById(R.id.indicator);
        startClock();

        indicator.generateAnim(indicator.getGraduateFloatList(70,250),40);
        rotateImage();

/*
                indicator = new Indicator(getActivity());
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        150);

                p.addRule(RelativeLayout.BELOW, R.id.image_disc);

                indicator.setDuration(20000);
                indicator.setBarNum(40);
                indicator.setStepNum(70);
                indicator.setBarColor(R.color.colorPrimary);
                indicator.setLayoutParams(p);
                relativeLayout.addView(indicator);
*/

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        btnBackgroundPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBgPlayEnabled = true;
                getFragmentManager().popBackStack();
            }
        });


        return view;
    }

    private void rotateImage() {
        RotateAnimation rotate = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(4000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.RESTART );

        imageDisc.startAnimation(rotate);
    }

    private void loadWebView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCod,String description, String failingUrl) {
                Log.e("error",description+"");
                isErrorFound = true;
            }

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                if(!isErrorFound){
                    layout.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
                }
                else
                    alertDialog(getActivity(), "Nothing to Stream from SwopLive","SwopLive");
            }

        });

        webView.loadUrl("http://184.154.43.106:8089/stream");
        Log.e("loading","loading");
        //webView.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })(http://184.154.43.106:8089/stream)");
    }

    public void alertDialog(final Activity activity, final String message, final String title) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(message);
        adb.setTitle(title);
        adb.setCancelable(false);
        adb.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().popBackStack();
            }
        });

        adb.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Pause","p");
        if(!isBgPlayEnabled)
            if(!isErrorFound && webView !=null)
                webView.destroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("Stop","p");

        Log.e("isbgplay",isBgPlayEnabled+"");
        if(!isBgPlayEnabled) {
            if (!isErrorFound && webView != null) {
                webView.destroy();
                webView = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Destroy","p");
        Log.e("isbgplay",isBgPlayEnabled+"");
        if(!isBgPlayEnabled) {
            Log.e("1","1");
            if (!isErrorFound && webView != null) {
                Log.e("2","2");
                webView.destroy();
                webView = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(webView!=null) {
            if (TextUtils.isEmpty(webView.getUrl()))
                loadWebView();
            else{
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        }
        else
            getFragmentManager().popBackStack();
    }

    private String getTime() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private void startClock() {
        mActive = true;
        new Thread(mRunnable).start();
    }
}