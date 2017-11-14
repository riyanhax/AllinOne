package com.parasme.swopinfo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.LocationActivity;
import com.parasme.swopinfo.helper.routemap.DrawMarker;
import com.parasme.swopinfo.helper.routemap.DrawRouteMaps;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by mukesh on 10/10/17.
 */

public class FragmentMap extends Fragment {

    public static AppCompatActivity appCompatActivity;
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
            appCompatActivity =(AppCompatActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AppCompatActivity){
            appCompatActivity =(AppCompatActivity) activity;
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
        WebServiceHandler webServiceHandler = new WebServiceHandler(appCompatActivity);
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

                        appCompatActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //setMarker(lat, lng);

                                drawRoute(lat, lng);
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

    private void drawRoute(double lat, double lng) {
        LatLng origin = new LatLng(-26.195246, 28.034088);
//        LatLng origin = new LatLng(LocationActivity.mCurrentLocation.getLatitude(), LocationActivity.mCurrentLocation.getLongitude());
        LatLng destination = new LatLng(lat, lng);
        DrawRouteMaps.getInstance(appCompatActivity)
                .draw(origin, destination, map);
        DrawMarker.getInstance(appCompatActivity).draw(map, origin, R.drawable.map, "Your Location");
        DrawMarker.getInstance(appCompatActivity).draw(map, destination, R.drawable.map, FragmentMap.this.getArguments().getString("company","SwopInfo"));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        appCompatActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 6);
        map.animateCamera(center);

    }


    private void setMarker(double lat, double lng){
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10);
        map.animateCamera(center);

        map.addMarker(new MarkerOptions().position(new LatLng(lat,
                lng)).title(FragmentMap.this.getArguments().getString("company","SwopInfo")));

    }
}