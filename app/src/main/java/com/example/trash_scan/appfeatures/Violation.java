package com.example.trash_scan.appfeatures;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.databinding.ActivityViolationBinding;
import com.example.trash_scan.databinding.FragmentViolationBinding;
import com.example.trash_scan.fragments.HomeFragment;
import com.example.trash_scan.violation.ViolationAdapter;
import com.example.trash_scan.violation.ViolationBodyActivity;
import com.example.trash_scan.violation.ViolationsAct;

import java.util.ArrayList;
import java.util.List;

public class Violation extends Fragment implements ViolationAdapter.ViolationAdapterLister {
   private ActivityViolationBinding binding;
    private List<ViolationsAct> arrayOfViolations;
    private ViolationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityViolationBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //yan mamili ka nalang
        binding.listViolation.setLayoutManager(new GridLayoutManager(view.getContext(),2));
/*
        binding.listViolation.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));*/


        // Construct the data source
        arrayOfViolations= new ArrayList<>();

        //violation 1
        arrayOfViolations.add(new ViolationsAct(
                "RA 9003 or the Ecological Solid Waste Management Act",
                getString(R.string.violation1)
        ));
        //violation 2
        arrayOfViolations.add(new ViolationsAct(
                "REPUBLIC ACT No. 9512 - National Environmental Awareness and Education Act of 2008",
                getString(R.string.violation2)
        ));

        arrayOfViolations.add(new ViolationsAct(
                "Republic Act No. 8749 - Philippine Clean Air Act of 1999 ",
                getString(R.string.violation3)
        ));

        arrayOfViolations.add(new ViolationsAct(
                "Republic Act No. 9275 - Philippine Clean Water Act of 2004",
                getString(R.string.violation4)
        ));
//violation 5
        arrayOfViolations.add(new ViolationsAct(
                "PRESIDENTIAL DECREE No. 1586",
                getString(R.string.violation5)
        ));

        arrayOfViolations.add(new ViolationsAct(
                "Republic Act No. 3931",
                getString(R.string.violation6)
        ));
        //violation 7
        arrayOfViolations.add(new ViolationsAct(
                "Republic Act No. 6969",
                getString(R.string.violation7)
        ));

        adapter = new ViolationAdapter(binding.getRoot().getContext(),arrayOfViolations,this);
        binding.listViolation.setAdapter(adapter);
    }

    @Override
    public void onContainerClick(int postion) {
        Intent intent=new Intent(requireContext(), ViolationBodyActivity.class);
        intent.putExtra("title" ,arrayOfViolations.get(postion).getTitle());
        intent.putExtra("body",arrayOfViolations.get(postion).getBody());
        startActivity(intent);
    }
}