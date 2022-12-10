package com.mobilapp.geotagging;


import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.mobilapp.geotagging.databinding.ActivityMainBinding;
import com.mobilapp.geotagging.databinding.FragmentFindAddressBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindAddressFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentFindAddressBinding binding;
    private Location currentLocation = new Location("Temp Location");
    // Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FindAddressFragmentDirections.ActionFindAddressFragmentToEditFragment action;

    // Location request
    private LocationRequest locationRequest;
    private Task<Location> currentLocationTask;

    public FindAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 99:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this.getContext(), "no permission!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindAddressFragment newInstance(String param1, String param2) {
        FindAddressFragment fragment = new FindAddressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        currentLocation.setLongitude(0);
        currentLocation.setLatitude(0);

        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "database1").allowMainThreadQueries().build();

        TagDao tagDao = db.tagDao(); // get tagDao (data access object)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        updatePermission();

        binding.buttonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nameLocation = binding.editName.getText().toString();


                if (!nameLocation.equals("")) {

                    updatePermission();

                    if (currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0) {

                        Tag newTag = new Tag(nameLocation, currentLocation.getLongitude(), currentLocation.getLatitude());

                        tagDao.insertNewTag(newTag);

                        Toast.makeText(binding.getRoot().getContext(), "Location Saved!", Toast.LENGTH_SHORT).show();

                        action = FindAddressFragmentDirections.actionFindAddressFragmentToEditFragment(tagDao.getIDFromName(nameLocation));
                        Navigation.findNavController(view).navigate(action);

                    } else {
                        Toast.makeText(binding.getRoot().getContext(), "Error grabbing location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), "Enter a name for your current location.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.buttonSaveTag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nameLocation = binding.editName.getText().toString();
                String longitude = binding.editLongitude.getText().toString();
                String latitude = binding.editLongitude.getText().toString();
                // Fix conditonal lol
                if (nameLocation.equals("")  || longitude.equals("") || latitude.equals("")) {
                    Toast.makeText(binding.getRoot().getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();

                } else {
                    Tag newTag = new Tag(nameLocation, Double.parseDouble(longitude), Double.parseDouble(latitude));
                    tagDao.insertNewTag(newTag);

                    action = FindAddressFragmentDirections.actionFindAddressFragmentToEditFragment(tagDao.getIDFromName(nameLocation));
                    Navigation.findNavController(view).navigate(action);
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    } // end on create view

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void updatePermission() {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                currentLocationTask = fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) { return null; }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }

            }).addOnSuccessListener((Activity) this.getContext(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    currentLocation = location;


                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }

    }


}