package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.applozic.mobicomkit.ApplozicClient;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentSettings extends Fragment {
    private AppCompatActivity mActivity;
    private LinearLayout layoutAccount;
    private ImageView imageUser;
    private Switch switchAppNotifications, switchChatNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        findViews(view);

        loadUserThumb();

        setSwitches();
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.replaceFragment(new FragmentProfile(), getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        switchAppNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("SWITCH",isChecked+"");
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_DISABLE_APP_NOTIFICATION, !isChecked);
            }
        });

        switchChatNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("SWITCHChat",isChecked+"");
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_ENABLE_CHAT_NOTIFICATION,isChecked);
                if(isChecked)
                    ApplozicClient.getInstance(getActivity()).enableNotification();
                else
                    ApplozicClient.getInstance(getActivity()).disableNotification();

            }
        });

        return view;
    }

    private void setSwitches() {
        switchAppNotifications.setChecked(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_DISABLE_APP_NOTIFICATION, false) ? false : true);
        switchChatNotifications.setChecked(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_ENABLE_CHAT_NOTIFICATION, false) ? true : false);
    }

    private void loadUserThumb() {
        String imageURL=AppConstants.URL_DOMAIN+"upload/user"+ AppConstants.USER_ID+"/profilepic.jpg";
        Picasso.with(mActivity).load(imageURL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .into(imageUser);

    }

    private void findViews(View view) {
        layoutAccount = (LinearLayout) view.findViewById(R.id.layout_account);
        imageUser = (ImageView) view.findViewById(R.id.imageUser);
        switchAppNotifications = (Switch) view.findViewById(R.id.switch_app_notifications);
        switchChatNotifications = (Switch) view.findViewById(R.id.switch_chat_notifications);
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
        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Settings");
    }


}