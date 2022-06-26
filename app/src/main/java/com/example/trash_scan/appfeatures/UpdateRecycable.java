package com.example.trash_scan.appfeatures;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.ProgressDialog;
import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentUpdateRecycableBinding;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.viewmodels.RecycableViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class UpdateRecycable extends DialogFragment {
    private FragmentUpdateRecycableBinding binding;
    private RecycableViewModel recycableViewModel;
    private Recycables recycables;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateRecycableBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        recycableViewModel = new ViewModelProvider(requireActivity()).get(RecycableViewModel.class);
        progressDialog =new ProgressDialog(requireActivity());
        recycableViewModel.getRecycables().observe(getViewLifecycleOwner(), recycables -> {
            this.recycables = recycables;
            binding.inputItemName.setText(recycables.getRecycableItemName());
            binding.inputPrice.setText(String.valueOf(recycables.getRecycablePrice()));
            binding.inputInformation.setText(recycables.getRecycableInformation());
            if (!recycables.getRecycalbleImage().isEmpty()) {
                Picasso.get().load(recycables.getRecycalbleImage()).into(binding.recycableImage);
            }
        });
        binding.buttonAddItem.setOnClickListener(v -> {
            String name = binding.inputItemName.getText().toString();
            String price = binding.inputPrice.getText().toString();
            String info = binding.inputInformation.getText().toString();
            if (name.isEmpty()) {
                binding.inputItemName.setError("enter name");
            }
            else if (price.isEmpty()) {
                binding.inputPrice.setError("enter price");
            }
            else if (info.isEmpty()) {
                binding.inputItemName.setError("enter info");
            } else  {
                recycables.setRecycableItemName(name);
                recycables.setRecycablePrice(Integer.parseInt(price));
                recycables.setRecycableInformation(info);

                Recycables updated = new Recycables(recycables.getRecycableID(),recycables.getJunkshopID(),recycables.getRecycalbleImage(),name,info,Integer.parseInt(price));
                updateRecycable(updated);
            }
        });


    }
    private void updateRecycable(Recycables recycables){
        progressDialog.isLoading();
        firestore.collection("Recycables")
                .document(recycables.getRecycableID())
                .set(recycables)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(binding.getRoot().getContext(),"Updated",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(binding.getRoot().getContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.stopLoading();
                    dismiss();
                });
    }
}