package com.mobilapp.geotagging;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobilapp.geotagging.databinding.FragmentExportBinding;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private HashMap<CheckBox,Integer> databaseKeys;

    private FragmentExportBinding binding;

    public ExportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExportFragment newInstance(String param1, String param2) {
        ExportFragment fragment = new ExportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        databaseKeys=new HashMap<CheckBox,Integer>();
        binding=FragmentExportBinding.inflate(getLayoutInflater());

        View view=binding.getRoot();
        //TODO: Populate tags_display with elements.
        //> Add the checkbox of each tag display to databaseKeys, mapped to the appropriate tag id.
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "database1").allowMainThreadQueries().build();

        TagDao tagDao = db.tagDao(); // get tagDao (data access object)
        List<Tag> tags = tagDao.getAll(); // get all tags
        //Create a test tag.
        /*if(tags.size()<10)
        {
            Random rng=new Random();
            Tag newTag=new Tag("Test"+tags.size(), rng.nextDouble()*90, rng.nextDouble()*90 );
            tagDao.insertNewTag(newTag);
        }*/
        for(Tag x:tags)
        {
            Log.d(ExportFragment.class.getSimpleName(), "Creating row for tag with id "+x.tid);
            LinearLayout newRow=new LinearLayout(getContext());
            newRow.setOrientation(LinearLayout.HORIZONTAL);
            newRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox=new CheckBox(getContext());
            checkBox.setText(x.tagName);
            Button edit_button=new Button(getContext());
            edit_button.setText(R.string.edit);
            databaseKeys.put(checkBox,x.tid);
            binding.tagsDisplay.addView(newRow);
            newRow.setVisibility(View.VISIBLE);
            newRow.addView(checkBox);
            checkBox.setVisibility(View.VISIBLE);
            newRow.addView(edit_button);
            edit_button.setVisibility(View.VISIBLE);
            edit_button.setEnabled(true);
            edit_button.setOnClickListener((View myView)->
                    NavHostFragment.findNavController(this).navigate(ExportFragmentDirections.actionExportFragmentToEditFragment(x.tid)));
        }


        //TODO: Add click listeners to each button.
        binding.allButton.setOnClickListener((View myView)->
                {
                    for(CheckBox x:databaseKeys.keySet())
                    {
                        x.setChecked(true);
                    }
                }
        );

        binding.deleteButton.setOnClickListener((View myView) ->
        {
            ArrayList<Integer> toDelete=getSelectedTagIDs();
            //TODO: Delete selected tags.

            for(Integer x:toDelete)
            {
                tagDao.deleteTagAtID(x);
            }

            NavHostFragment.findNavController(this).navigate(ExportFragmentDirections.actionExportFragmentSelf());
        });

        binding.displayButton.setOnClickListener((View myView)->
        {
            ArrayList<Integer> toDisplayBase=getSelectedTagIDs();

            int[] toDisplay=new int[toDisplayBase.size()];
            for(int i=0;i<toDisplayBase.size();i++)
            {
                toDisplay[i]= toDisplayBase.get(i);
            }

            if(toDisplay.length==0)
            {
                toDisplay=new int[]{-1};
            }

            NavHostFragment.findNavController(this).navigate(ExportFragmentDirections.actionExportFragmentToMapFragment(toDisplay));
        });

        binding.exportButton.setOnClickListener((View myView)->
        {
            ArrayList<Integer> toExportKeys=getSelectedTagIDs();
            String toExport="";
            //Convert each tag to CSV format.
            for(Integer x:toExportKeys)
            {
                Tag tag=tagDao.getTagByID(x);
                toExport+=tag.tagName+",";
                toExport+=tag.latitude+",";
                toExport+=tag.longitude+",";
                toExport+=tag.distance+";";
            }

            //TODO: Export this to a file.
            try {
                FileOutputStream fos;
                fos = getContext().openFileOutput(getString(R.string.export_filepath), Context.MODE_PRIVATE);
                fos.write(toExport.getBytes());
            }
            catch(Exception e)
            {
                e.getStackTrace();
            }


        });

        binding.newTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_exportFragment_to_findAddressFragment);
            }
        });

        return view;
    }


    public ArrayList<Integer> getSelectedTagIDs()
    {
        ArrayList<Integer> result=new ArrayList<>();
        for(CheckBox x:databaseKeys.keySet())
        {
            if(x.isChecked())
            {
                result.add(databaseKeys.get(x));
            }
        }
        return result;
    }
}