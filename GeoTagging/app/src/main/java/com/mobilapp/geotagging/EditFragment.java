package com.mobilapp.geotagging;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.room.Room;

import com.mobilapp.geotagging.databinding.FragmentEditBinding;

public class EditFragment extends Fragment {
    private FragmentEditBinding binding;
    private int id;
    private EditFragmentDirections.ActionEditFragmentToMapFragment action;


    public EditFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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