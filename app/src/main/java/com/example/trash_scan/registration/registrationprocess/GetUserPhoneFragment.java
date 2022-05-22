package com.example.trash_scan.registration.registrationprocess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.viewmodel.OtpSharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class GetUserPhoneFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private OtpSharedViewModel otpSharedViewModel;
    private TextInputLayout inputPhoneNumber;
    private Button buttonNext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_user_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        otpSharedViewModel = new ViewModelProvider(requireActivity()).get(OtpSharedViewModel.class); //Initialize OtpSharedViewModel Class
        firebaseAuth = FirebaseAuth.getInstance(); //Initialize Firebase authentication
        buttonNext.setOnClickListener(v -> {
            String phone = inputPhoneNumber.getEditText().getText().toString();
            if (phone.isEmpty()){
                inputPhoneNumber.setError("please enter your phone number");
            } else if (!phone.startsWith("9")){
                inputPhoneNumber.setError("Invalid Number");
            } else {
                sendVerificationCode(phone);
            }
        });
    }
    /**
     * this method Sends Verification code to the user's phone number
     * @param phone This parameter is from the user input.
     */
    private void sendVerificationCode(String phone){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+63" + phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                        Snackbar.make(requireView(),"Success" , Snackbar.LENGTH_LONG).show();
                    }
                    @Override
                    public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                        Snackbar.make(requireView(),"Failed" , Snackbar.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        otpSharedViewModel.setPhoneNumber(phone);
                        otpSharedViewModel.setCode(s);
                        Navigation.findNavController(requireView()).navigate(R.id.action_getUserPhoneFragment_to_userPhoneVerificationFragment);

                    }
                }) // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void initViews(View view) {
        inputPhoneNumber = view.findViewById(R.id.inputPhoneNumber);
        buttonNext = view.findViewById(R.id.buttonSendOTP);
    }
}