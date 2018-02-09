package com.parasme.swopinfo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;

import java.util.ArrayList;

/**
 * Created by mukesh on 18/1/18.
 */

public class NewVersionActivity extends AppCompatActivity {

    private TextView textInstalledVersion;
    private TextView textCurrentOfficialVersion;
    private Button btnUpdate;
    private ListView listWhatsNew;
    private ArrayList<String> whatsNewList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_version);

        findViews();
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.text_title)).setText("SwopInfo");

        setData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),1);
//                    finish();
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),1);
//                    finish();
                }
            }
        });
    }


    private void findViews() {
        textInstalledVersion = (TextView) findViewById(R.id.textInstalledVersion);
        textCurrentOfficialVersion = (TextView) findViewById(R.id.textNewVersion);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        listWhatsNew = (ListView) findViewById(R.id.listWhatsNew);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setData() {
        PackageInfo pInfo = null;
        try {
            pInfo = NewVersionActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            textInstalledVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {e.printStackTrace();}

        textCurrentOfficialVersion.setText(getIntent().getStringExtra("newVersion"));
        whatsNewList = (ArrayList<String>) getIntent().getSerializableExtra("whatsNew");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewVersionActivity.this, R.layout.row_whats_new, whatsNewList);
        listWhatsNew.setAdapter(arrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            Intent intent = new Intent(NewVersionActivity.this, SplashActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            startActivity(intent);
            finish();
        }
    }
}