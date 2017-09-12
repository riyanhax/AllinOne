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
import android.widget.ImageView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;

/**
 * Created by :- Mukesh Kumawat on 10-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class BaseFragment extends Fragment implements View.OnClickListener{

    protected View baseView;
    protected AppCompatActivity mActivity;
    protected ImageView imageActionFeed,imageActionUploads,imageActionFollowing,imageActionFollower,imageActionGroups,
            imageActionBookmarks;
    public static boolean isFollowersLoaded;
    public static boolean isFollowingLoaded;
    public static boolean isGroupsLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_base, container, false);

        imageActionFollower =  (ImageView) baseView.findViewById(R.id.imageActionFollower);
        imageActionUploads =  (ImageView) baseView.findViewById(R.id.imageActionUploads);
        imageActionFeed =  (ImageView) baseView.findViewById(R.id.imageActionFeed);
        imageActionFollowing =  (ImageView) baseView.findViewById(R.id.imageActionFollowing);
        imageActionGroups =  (ImageView) baseView.findViewById(R.id.imageActionGroups);
        imageActionBookmarks =  (ImageView) baseView.findViewById(R.id.imageActionBookmarks);

        imageActionFollower.setOnClickListener(this);
        imageActionUploads.setOnClickListener(this);
        imageActionFeed.setOnClickListener(this);
        imageActionFollowing.setOnClickListener(this);
        imageActionGroups.setOnClickListener(this);
        imageActionBookmarks.setOnClickListener(this);

        return baseView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity){
            mActivity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity){
            mActivity = (AppCompatActivity) activity;
        }
    }

    @Override
    public void onClick(View view) {

        Bundle bundle=new Bundle();
        Fragment fragment = null;
        switch (view.getId()){
            case R.id.imageActionFollowing:
                Log.e("AAA", "onClick: " );
                fragment=new FragmentFollowing();
                bundle.putString(AppConstants.KEY_USER_ID,AppConstants.USER_ID);
                fragment.setArguments(bundle);
                break;
            case R.id.imageActionFollower:
                fragment=new FragmentFollowers();
                bundle.putString(AppConstants.KEY_USER_ID,AppConstants.USER_ID);
                fragment.setArguments(bundle);
                break;

            case R.id.imageActionFeed:
                fragment=new FragmentHome();
                break;
            case R.id.imageActionUploads:
                fragment=new FragmentUploadsWrapper();
                break;
            case R.id.imageActionGroups:
                isGroupsLoaded = false;
                fragment=new FragmentGroups();
                bundle.putString(AppConstants.KEY_USER_ID, AppConstants.USER_ID);
                fragment.setArguments(bundle);
                break;
            case R.id.imageActionBookmarks:
                fragment=new FragmentBookmarks();
                break;
        }
        MainActivity.replaceFragment(fragment,getFragmentManager(),mActivity,R.id.content_frame);
    }
}