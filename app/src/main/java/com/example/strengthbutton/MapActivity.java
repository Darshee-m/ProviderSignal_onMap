package com.example.strengthbutton;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap=googleMap;
        MarkerOptions markerOptions = new MarkerOptions();

        Toast.makeText(this, "on map ready", Toast.LENGTH_SHORT).show();
        gmap.addMarker(new MarkerOptions()
                .position(new LatLng(19, 73))
                .title("Hello world"));
        LatLng india=new LatLng(19,73);
        float zoomlevel=(float)10.0;
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,zoomlevel));
    }

    public static final String FINE_LOACTION=Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;
    //vars
    private Boolean mLocation =false;
    public  GoogleMap gmap;
    public Button btn;

    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        //btn=(Button)findViewById(R.id.next);
    }
//    public void nextt(View v){
//        LatLng india=new LatLng(19.2020801,73.1610635);
//        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
//                india, 15);
//        gmap.animateCamera(location);
//        Toast.makeText(this, "next clicked", Toast.LENGTH_SHORT).show();
//        float zoomlevel=(float)18.0;
//        gmap.addMarker(new MarkerOptions()
//                .position(new LatLng(19.2020801,73.1610635))
//                .title("fuck world"));
//
//
//    }
    public void initMap(){
        Toast.makeText(this, "inside map", Toast.LENGTH_SHORT).show();

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    private void getLocationPermission(){
        Toast.makeText(this, "getting permission", Toast.LENGTH_SHORT).show();
        String[] permissions= {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    FINE_LOACTION) == PackageManager.PERMISSION_GRANTED) {
                mLocation = true;
                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permissions, 1234);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this, "onrequest", Toast.LENGTH_SHORT).show();
        mLocation=false;
        switch(requestCode){
            case 1234:{
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            mLocation=false;
                            return;
                        }
                    }

                    mLocation=true;
                }
                //initialize our map
                initMap();
            }
        }
    }


}
