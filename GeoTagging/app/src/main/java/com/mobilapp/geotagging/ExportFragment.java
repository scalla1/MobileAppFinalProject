package com.mobilapp.geotagging;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.mobilapp.geotagging.databinding.FragmentExportBinding;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ExportFragment extends Fragment {
    private HashMap<CheckBox,Integer> databaseKeys;
    private FragmentExportBinding binding;

    public ExportFragment() {
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
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

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
                toExport+=tag.tagName+",\t";
                toExport+=tag.latitude+",\t";
                toExport+=tag.longitude+",\t";
                toExport+=tag.distance+";\n";
            }

            //TODO: Export this to a file.
            try {
                String pathname;
                pathname="/storage/emulated/0/Documents/export.csv";
                File file=new File(pathname);
                FileOutputStream fos=new FileOutputStream(file);
                fos.write(toExport.getBytes());
                fos.close();
                Toast.makeText(getContext(),
                        "File exported.\n"+pathname,
                        Toast.LENGTH_SHORT).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(), "Problem exporting file.",Toast.LENGTH_SHORT).show();
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