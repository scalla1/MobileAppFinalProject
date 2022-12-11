package com.mobilapp.geotagging;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mobilapp.geotagging.databinding.FragmentEditBinding;
import com.mobilapp.geotagging.databinding.FragmentFindAddressBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentEditBinding binding;
    private int id;
    private EditFragmentDirections.ActionEditFragmentToMapFragment action;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
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
        binding = FragmentEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        id  = EditFragmentArgs.fromBundle(requireArguments()).getTagID();

        // make database (might have to pass between fragments)
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "database1").allowMainThreadQueries().build();

        TagDao tagDao = db.tagDao(); // get tagDao (data access object)

        Tag tag = tagDao.getTagByID(id); // get specific tag

        binding.editTextTagName.setText(tag.tagName);
        binding.editTextLatitude.setText(String.valueOf(tag.latitude));
        binding.editTextLongitude.setText(String.valueOf(tag.longitude));


        binding.buttonSaveTagEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tagName = binding.editTextTagName.getText().toString();
                String latitude = binding.editTextLatitude.getText().toString();
                String longitude = binding.editTextLongitude.getText().toString();

                if (tagName.equals("") || latitude.equals("") || longitude.equals("")) {
                    Toast.makeText(binding.getRoot().getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    int tempID;
                    tagDao.deleteTagAtID(id);
                    Tag newTag = new Tag(tagName, Double.parseDouble(longitude), Double.parseDouble(latitude));
                    tagDao.insertNewTag(newTag);
                    tempID = tagDao.getIDFromName(tagName);
                    tagDao.setIDwithID(id, tempID);
                    Toast.makeText(binding.getRoot().getContext(), "Tag Saved!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.buttonBackToSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_editFragment_to_exportFragment);
            }
        });

        binding.buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] idSend = {id};
                action = EditFragmentDirections.actionEditFragmentToMapFragment(idSend);
                Navigation.findNavController(view).navigate(action);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}