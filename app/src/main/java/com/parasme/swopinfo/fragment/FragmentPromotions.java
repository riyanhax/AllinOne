package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.PromotionsAdapter;
import com.parasme.swopinfo.model.Category;

import java.util.ArrayList;

/**
 * Created by :- Mukesh Kumawat on 12-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public class FragmentPromotions extends Fragment {

    private AppCompatActivity mActivity;
    private ListView listCategories;
    private ArrayList<Category> categoryArrayList;
    private MenuItem itemSearch, itemFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_promotions, container, false);
        setHasOptionsMenu(true);
        findViews(view);

        categoryArrayList = new ArrayList<>();

        listCategories.setAdapter(new PromotionsAdapter(mActivity, R.layout.row_promotion, categoryArrayList));

        listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.replaceFragment(new FragmentPromotionPager(), getFragmentManager(), mActivity, R.id.content_frame);

            }
        });
        return view;
    }



    private void findViews(View view) {
        listCategories = (ListView) view.findViewById(R.id.list_promotions);
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
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        itemSearch= menu.findItem(R.id.menu_search);
        itemFavorite = menu.findItem(R.id.menu_done);
        itemFavorite.setTitle("Favorites");
        itemSearch.setVisible(false);
        itemFavorite.setVisible(true);
        itemFavorite.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity.replaceFragment(new FragmentFavourites(), getFragmentManager(), mActivity, R.id.content_frame);
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        itemSearch.setVisible(true);
        itemFavorite.setVisible(false);
    }
}