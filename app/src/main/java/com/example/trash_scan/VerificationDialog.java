package com.example.trash_scan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.databinding.FragmentVerificationDialogBinding;
import com.example.trash_scan.registration.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class VerificationDialog extends DialogFragment {
    private FragmentVerificationDialogBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerificationDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.buttonVerifyNow.setOnClickListener(view1 -> {
            if (currentUser != null) {
                if (currentUser.isEmailVerified()) {
                    dismiss();
                } else {
                    sendVerification(currentUser);
                }
            }
        });
        binding.buttonLogout.setOnClickListener(view12 -> {
            FirebaseAuth.getInstance().signOut();
            dismiss();
        });

    }
    private void sendVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                verificationCodeCountDown();
                Toast.makeText(binding.getRoot().getContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else  {
                Toast.makeText(binding.getRoot().getContext(), "Failed to send verification email." + user.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Verification Countdown. (60 seconds)
    private void verificationCodeCountDown(){
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.buttonVerifyNow.setText("" + millisUntilFinished / 1000);
                binding.buttonVerifyNow.setEnabled(false); //Disable the button to prevent multiple clicks
                binding.textEmailVerified.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                binding.buttonVerifyNow.setText("Verify now");
                binding.buttonVerifyNow.setEnabled(true); //Enable the button once the countdown is finish
                binding.textEmailVerified.setVisibility(View.GONE);
            }
        }.start();
    }


}