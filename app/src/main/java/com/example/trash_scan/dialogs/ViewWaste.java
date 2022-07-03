package com.example.trash_scan.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.JunkShopsWasteAdapter;
import com.example.trash_scan.databinding.FragmentViewWasteBinding;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.viewmodels.RecycableViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ViewWaste extends DialogFragment implements JunkShopsWasteAdapter.OnWasteClick {

    private FragmentViewWasteBinding binding;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewWasteBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        recycableViewModel = new ViewModelProvider(requireActivity()).get(RecycableViewModel.class);
        recycableViewModel.getRecycables().observe(getViewLifecycleOwner(), recycables -> {
            if (!recycables.getRecycalbleImage().isEmpty()) {
                Picasso.get().load(recycables.getRecycalbleImage()).into(binding.wasteImage);
            }
            binding.textWasteName.setText(recycables.getRecycableItemName());
            binding.textDesc.setText(recycables.getRecycableInformation());
            binding.textPrice.setText(String.valueOf(recycables.getRecycablePrice()));
            if (!recycables.getJunkshopID().isEmpty()){
                getRecyclableOwner(recycables.getJunkshopID());
            }
        });
        getJunkshopRecyclables();
        binding.buttonBack.setOnClickListener(v -> dismiss());
    }
    private void getRecyclableOwner(String id) {
        firestore.collection(User.TABLE_NAME)
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            bindUserInfo(user);
                        }
                    }
                });
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

    private void bindUserInfo(User user) {
        if (!user.getUserProfile().isEmpty()) {
            Picasso.get().load(user.getUserProfile()).into(binding.imageUserProfile);
        }
        String fullname= user.getUserFirstName() + " " + user.getUserLastName();
        binding.junkShopOwnerName.setText(fullname);
        binding.junkshopAddress.setText(user.getUserAddress());
        binding.textPhone.setText(user.getUserPhoneNumber());
        binding.textEmail.setText(user.getUserEmail());
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