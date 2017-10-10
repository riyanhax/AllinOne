package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LocationActivity;

/**
 * Created by mukesh on 10/10/17.
 */

public class FragmentMap extends Fragment {

    private AppCompatActivity mActivity;
    private MapView mapView;
    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        initAndLoadMap(savedInstanceState);

        return view;
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

    private void initAndLoadMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if(map!=null){

                    map.getUiSettings().setZoomControlsEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);

                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.mCurrentLocation.getLatitude(), LocationActivity.mCurrentLocation.getLongitude()), 10);
                    map.animateCamera(center);

                        map.addMarker(new MarkerOptions().position(new LatLng(LocationActivity.mCurrentLocation.getLatitude(),
                                LocationActivity.mCurrentLocation.getLongitude())).title(FragmentMap.this.getArguments().getString("company","SwopInfo")));

                }

            }


        });
    }

}
