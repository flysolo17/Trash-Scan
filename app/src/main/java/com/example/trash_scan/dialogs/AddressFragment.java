package com.example.trash_scan.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentAddressBinding;
import com.example.trash_scan.firebase.models.Address;
import com.example.trash_scan.viewmodels.AddressViewModel;

public class AddressFragment extends DialogFragment {

    private FragmentAddressBinding binding;
    private String[] barangays;
    private AddressViewModel addressViewModel;
    private Address address;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addressViewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        addressViewModel.getAddress().observe(getViewLifecycleOwner(), address -> {
            this.address = address;
        });
        binding = FragmentAddressBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barangays = requireActivity().getResources().getStringArray(R.array.barangays);
        ArrayAdapter adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item,barangays);
        binding.textBarangay.setAdapter(adapter);
        binding.buttonSaveAddress.setOnClickListener(v -> {
            String barangay = binding.textBarangay.getSelectedItem().toString();
            String houseNo = binding.inputHouseNumber.getText().toString();
            if (barangay.isEmpty()) {
                Toast.makeText(view.getContext(), "enter barangay", Toast.LENGTH_SHORT).show();
            }
            else if (houseNo.isEmpty()) {
                binding.inputHouseNumber.setError("Enter house no.");
            } else {
                Address address = new Address(barangay,houseNo);
                addressViewModel.setAddress(address);
                dismiss();
            }
        });
    }

}