package com.example.trash_scan.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentTermsAndConditionsBinding;
import com.example.trash_scan.registration.RegistrationActivity;


public class TermsAndConditions extends DialogFragment {

    private FragmentTermsAndConditionsBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTermsAndConditionsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.web.loadUrl("file:///android_asset/terms.html");
        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                binding.buttonAccept.setEnabled(true);
            } else  {
                binding.buttonAccept.setEnabled(false);
            }
        });
        binding.buttonAccept.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RegistrationActivity.class));
            dismiss();
        });
    }
}