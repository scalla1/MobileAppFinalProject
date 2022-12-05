package com.mobilapp.geotagging;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilapp.geotagging.databinding.ActivityMainBinding;
import com.mobilapp.geotagging.databinding.FragmentFindAddressBinding;

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

    public FindAddressFragment() {
        // Required empty public constructor
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



        binding.buttonConvertAddress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String addressForLocation = binding.editTextAddress.toString();
                Geocoder geocode = new Geocoder(view.getContext(), Locale.getDefault());
                //List<Address> addList =  geocode.getFromLocationName(addressForLocation, 1);

            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}