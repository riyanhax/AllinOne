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
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by mukesh on 10/10/17.
 */

public class FragmentMap extends Fragment {

    private AppCompatActivity mActivity;
    private MapView mapView;
    private GoogleMap map;
    private String address;

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

                    geoCodeAddress();

                }

            }


        });
    }

    private void geoCodeAddress() {
        String address = this.getArguments().getString("address");
        address = address.replaceAll(" ","%20");
        Log.e("Geocodeadd",address);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+address+"&key=AIzaSyABGigUX5fvihfR8Bc8WnCScS3XXWK2B78";
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e("GEOCODERE",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("status").equalsIgnoreCase("ok")){
                        JSONObject locationObject = jsonObject.optJSONArray("results").optJSONObject(0)
                                .optJSONObject("geometry").optJSONObject("location");

                        final double lat = locationObject.optDouble("lat");
                        final double lng = locationObject.optDouble("lng");

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMarker(lat, lng);
                            }
                        });
                    }
                }catch (JSONException e){e.printStackTrace();}
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setMarker(double lat, double lng){
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10);
        map.animateCamera(center);

        map.addMarker(new MarkerOptions().position(new LatLng(lat,
                lng)).title(FragmentMap.this.getArguments().getString("company","SwopInfo")));

    }
}