package com.mobilapp.geotagging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    // Location request
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // set all properties of Location Request
        locationRequest = new LocationRequest();

        // how often does the default location check occur?
        locationRequest.setInterval(1000 * 30);

        //how often does the location check occur when set to the most frequest update
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
}