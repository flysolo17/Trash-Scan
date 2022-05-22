package com.example.trash_scan.appfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.fragments.HomeFragment;
import com.example.trash_scan.violation.ViolationAdapter;
import com.example.trash_scan.violation.ViolationBodyActivity;
import com.example.trash_scan.violation.ViolationsAct;

import java.util.ArrayList;
import java.util.List;

public class Violation extends AppCompatActivity implements ViolationAdapter.ViolationAdapterLister {
    private RecyclerView list_violation;
    private List<ViolationsAct> arrayOfViolations;
    private ViolationAdapter adapter;
    private ImageView button_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);

        initViews();
        list_violation.setLayoutManager(new LinearLayoutManager(this));
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






        adapter = new ViolationAdapter(this, arrayOfViolations, this);
        list_violation.setAdapter(adapter);
    }
    private void initViews(){
        list_violation = findViewById(R.id.list_violation);
        button_back = findViewById(R.id.button_back);
    }

    @Override
    public void onContainerClick(int postion) {
        Intent intent=new Intent(Violation.this, ViolationBodyActivity.class);
        intent.putExtra("title" ,arrayOfViolations.get(postion).getTitle());
        intent.putExtra("body",arrayOfViolations.get(postion).getBody());
        startActivity(intent);
    }
}