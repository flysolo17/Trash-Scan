package com.example.trash_scan.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.JunkShopsWasteAdapter;
import com.example.trash_scan.databinding.FragmentMarketPlaceBinding;
import com.example.trash_scan.dialogs.ViewWaste;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.viewmodels.RecycableViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class MarketPlaceFragment extends DialogFragment implements JunkShopsWasteAdapter.OnWasteClick {

    private FragmentMarketPlaceBinding binding;
    private RecycableViewModel recycableViewModel;
    private FirebaseFirestore firestore;
    private List<Recycables> recyclablesList;
    private JunkShopsWasteAdapter adapter;

    private void init() {
        firestore = FirebaseFirestore.getInstance();
        recyclablesList = new ArrayList<>();
        binding.recyclerviewWaste.setLayoutManager(new GridLayoutManager(binding.getRoot().getContext(),2,GridLayoutManager.VERTICAL,false));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMarketPlaceBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        recycableViewModel = new ViewModelProvider(requireActivity()).get(RecycableViewModel.class);
        getJunkshopRecyclables();
        binding.buttonBack.setOnClickListener(v -> dismiss());
    }
    private void getJunkshopRecyclables(){
        recyclablesList.clear();
        firestore.collection(Recycables.TABLE_NAME)
                .addSnapshotListener((value, error) -> {
                    if (error  != null) {
                        Log.d(".TrashActivity",error.getMessage());
                    }
                    if (value != null) {
                        for (QueryDocumentSnapshot snapshot : value){
                            if (snapshot != null) {
                                Recycables recycables = snapshot.toObject(Recycables.class);
                                recyclablesList.add(recycables);
                            }
                        }
                        adapter = new JunkShopsWasteAdapter(binding.getRoot().getContext(),recyclablesList,this);
                        binding.recyclerviewWaste.setAdapter(adapter);
                    }
                });
    }
    @Override
    public void onViewWasteInfo(int position) {
        ViewWaste viewWaste = new ViewWaste();
        if (!viewWaste.isAdded()) {
            recycableViewModel.setRecycables(recyclablesList.get(position));
            viewWaste.show(getChildFragmentManager(),"View Waste Info");
        }
    }
}