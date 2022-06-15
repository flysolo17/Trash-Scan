package com.example.trash_scan.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentChangePasswordBinding;
import com.example.trash_scan.databinding.FragmentForgotPasswordBinding;
import com.example.trash_scan.dialogs.TermsAndConditions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;


public class ChangePasswordFragment extends DialogFragment {
    private FragmentChangePasswordBinding binding;
    private Validation validation;

    private FirebaseAuth firebaseAuth;
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
        binding = FragmentChangePasswordBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validation = new Validation();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.buttonChangePassword.setOnClickListener(v -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String oldPassword = binding.inputCurrentPassword.getEditText().getText().toString();
            if (oldPassword.isEmpty()) {
                Toast.makeText(view.getContext(), "enter current password!", Toast.LENGTH_SHORT).show();
            } else {
                authenticateOldPassword(user,oldPassword);
            }
        });
    }
    private void authenticateOldPassword(FirebaseUser user,String oldPassword) {
        if (user != null && user.getEmail() != null) {
            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
            user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String newPassword = binding.inputPassword.getEditText().getText().toString();
                    String confirmPassword = binding.inputConfirmPassword.getEditText().getText().toString();
                    if (newPassword.length() < 7) {
                        binding.inputCurrentPassword.setError("Password too weak!");
                    }
                    else if (!newPassword.equals(confirmPassword)) {
                        binding.inputConfirmPassword.setError("Password didn't match!");
                    } else {
                        updatePassword(user,newPassword);
                    }
                } else  {
                    binding.inputCurrentPassword.getEditText().setError("Invalid password");
                }
            });
        }
    }
    private void updatePassword(FirebaseUser user,String newPassword){
        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(binding.getRoot().getContext(), "Password updated Successfully", Toast.LENGTH_SHORT).show();
                showSuccessDialog();
            } else {
                Toast.makeText(binding.getRoot().getContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(binding.getRoot().getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void showSuccessDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(binding.getRoot().getContext());
        alertDialog
                .setTitle("Password successfully updated")
                .setMessage("Do you want to continue logged in ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dismiss();
                    dialog.dismiss();
                }).setNegativeButton("Logout",(dialog,which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireActivity(),Login.class));
        }).show();
    }

}