
package com.mobilapp.geotagging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsFragment extends Fragment {
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "database1").allowMainThreadQueries().build();
            TagDao tagDao = db.tagDao();
            //Tag thisTag = new Tag( "sydney", 151, -34); //make tag with parameters
            //Tag thisTag2 = new Tag( "test",  111,  -54); //make tag with parameters
            //tagDao.insertNewTag(thisTag);
            //tagDao.insertNewTag(thisTag2);
            List<Tag> tags = tagDao.getAll(); // get all tags


            int[] arrId;
            arrId = MapsFragmentArgs.fromBundle(requireArguments()).getTagIDs();

            if(arrId[0] == -1){
                for(int i=0; i< tags.size(); i++){
                    Tag tag = tags.get(i);
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(tag.latitude, tag.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(tag.tagName));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(tag.latitude, tag.longitude)));
                }
            }
            else{
                for(int i=0; i<arrId.length; i++){
                    Tag tag = tagDao.getTagByID(arrId[i]); // get specific tag
                    LatLng pos = new LatLng(tag.latitude, tag.longitude);
                    googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(tag.tagName));
                }
            }


            //LatLng sydney = new LatLng(-34, 151);
            //LatLng test = new LatLng(-54, 111);
            //googleMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Marker in Sydney"));
            //googleMap.addMarker(new MarkerOptions().position(test).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("test"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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