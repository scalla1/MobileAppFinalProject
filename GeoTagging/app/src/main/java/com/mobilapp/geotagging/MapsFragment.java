
package com.mobilapp.geotagging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsFragment extends Fragment {
    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "database1").allowMainThreadQueries().build();
            TagDao tagDao = db.tagDao();
            List<Tag> tags = tagDao.getAll(); // get all tags
            CircleOptions circleOptions = new CircleOptions();

            float[] results = new float[1];
            int[] arrId;
            arrId = MapsFragmentArgs.fromBundle(requireArguments()).getTagIDs();

            if(arrId[0] == -1){
                for(int i=0; i< tags.size(); i++){
                    Tag tag = tags.get(i);
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(tag.latitude, tag.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(tag.tagName));
                    circleOptions.center(new LatLng(tag.latitude, tag.longitude));
                    circleOptions.radius(10000); //distance in meters
                    circleOptions.strokeColor(Color.BLACK);
                    circleOptions.fillColor(0x30ff0000);
                    circleOptions.strokeWidth(2);
                    googleMap.addCircle(circleOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(tag.latitude, tag.longitude)));

                    for (int j=1; j< tags.size(); j++){
                        Tag tag2 = tags.get(j);
                        if(!tag.tagName.equals(tag2.tagName)) {
                            Location.distanceBetween(tag.latitude, tag.longitude, tag2.latitude, tag2.longitude, results);
                            if (results[0] <= 10000) {
                                String str = tag.tagName + " is within 10km " + tag2.tagName;
                                Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }


            }
            else{
                for(int i=0; i<arrId.length; i++){
                    Tag tag = tagDao.getTagByID(arrId[i]); // get specific tag
                    LatLng pos = new LatLng(tag.latitude, tag.longitude);
                    googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(tag.tagName));
                    circleOptions.center(new LatLng(tag.latitude, tag.longitude));
                    circleOptions.radius(10000);
                    circleOptions.strokeColor(Color.BLACK);
                    circleOptions.fillColor(0x30ff0000);
                    circleOptions.strokeWidth(2);
                    googleMap.addCircle(circleOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(tag.latitude, tag.longitude)));

                    for (int j=1; j< tags.size(); j++){
                        Tag tag2 = tags.get(j);
                        if(!tag.tagName.equals(tag2.tagName)) {
                            Location.distanceBetween(tag.latitude, tag.longitude, tag2.latitude, tag2.longitude, results);
                            if (results[0] <= 10000) {
                                String str = tag.tagName + " is within 10km " + tag2.tagName;
                                Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}