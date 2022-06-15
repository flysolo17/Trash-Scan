package com.example.trash_scan.registration;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentForgotPasswordBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;


public class ForgotPasswordFragment extends DialogFragment {
    private FragmentForgotPasswordBinding binding;
    private Validation validation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validation = new Validation();
        binding.buttonResetPassword.setOnClickListener(v-> {
            forgotPassword(binding.inputEmail);
        });
    }

    private void forgotPassword(TextInputLayout email) {
        if (!validation.validateEmail(email)) {
            Toast.makeText(binding.inputEmail.getContext(), "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getEditText().getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(binding.getRoot().getContext(), "Check your email to reset your password",Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(binding.getRoot().getContext(), "Try again! something wrong happened!",Toast.LENGTH_SHORT).show();
                        }
            });
        }
    }
}