package com.mobilapp.geotagging;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.mobilapp.geotagging.databinding.FragmentExportBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        for(Tag x:tags)
        {
            LinearLayout newRow=new LinearLayout(getContext());
            newRow.setOrientation(LinearLayout.HORIZONTAL); //fix
            CheckBox checkBox=new CheckBox(getContext());
            checkBox.setText(x.tagName);
            Button edit_button=new Button(getContext());
            edit_button.setText(R.string.edit);
            databaseKeys.put(checkBox,x.tid);
            ArrayList<View> layoutArray=new ArrayList<>();
            layoutArray.add(newRow);
            binding.tagsDisplay.addChildrenForAccessibility(layoutArray); //fix
            ArrayList<View> children=new ArrayList<>();
            children.add(checkBox);
            children.add(edit_button);
            newRow.addChildrenForAccessibility(children);
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