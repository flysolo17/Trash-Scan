package com.example.trash_scan.registration.registrationprocess;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.ProgressDialog;
import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentCreateUserAccountBinding;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.Login;
import com.example.trash_scan.registration.viewmodel.OtpSharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreateUserAccountFragment extends Fragment {

    private OtpSharedViewModel otpSharedViewModel;
    private FragmentCreateUserAccountBinding binding;
    private String phone_number = "";
    private FirebaseFirestore firebaseFirestore;
    String userType = "home owner";
    ProgressDialog progressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateUserAccountBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        firebaseFirestore = FirebaseFirestore.getInstance();
        otpSharedViewModel = new ViewModelProvider(requireActivity()).get(OtpSharedViewModel.class); //Initialize OtpSharedViewModel
        otpSharedViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> {
            //Get Phone number
            phone_number = phoneNumber.toString();
        });
        binding.toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonHomeOwner) {
                    userType = "home owner";
                } else if (checkedId == R.id.buttonJunkShopOwner){
                    userType = "junk shop owner";
                }
            } else if (group.getCheckedButtonId() == View.NO_ID){
                userType = "home owner";
            } else {
                userType = "home owner";
            }
        });

        binding.buttonCreateAccount.setOnClickListener(v -> {
            createAccount();

        });
    }

    private void createAccount(){
        if (binding.inputFirstName.getEditText().getText().toString().isEmpty()){
            binding.inputFirstName.setError("Input Firstname");
        } else if (binding.inputLastName.getEditText().getText().toString().isEmpty()){
            binding.inputLastName.setError("Input Lastname");
        } else if (binding.inputAddress.getEditText().getText().toString().isEmpty()) {
            binding.inputAddress.setError("Input Email");
        }else if (binding.inputPassword.getEditText().getText().toString().length() < 8){
            binding.inputPassword.setError("Password too short");
        } else if (!binding.inputPassword.getEditText().getText().toString().equals(binding.inputConfirmPassword.getEditText().getText().toString())){
            binding.inputConfirmPassword.setError("Password dont match");
        } else {
            progressDialog.isLoading();
            User user = new User();
            user.setUserID(firebaseFirestore.collection(User.TABLE_NAME).getId());
            user.setUserFirstName(binding.inputFirstName.getEditText().getText().toString());
            user.setUserLastName(binding.inputLastName.getEditText().getText().toString());
            user.setUserAddress(binding.inputAddress.getEditText().getText().toString());
            user.setUserEmail(binding.inputEmail.getEditText().getText().toString());
            user.setUserPhoneNumber(phone_number);
            user.setUserType(userType);

            firebaseFirestore.collection(User.TABLE_NAME).document(firebaseFirestore.collection(User.TABLE_NAME).document().getId()).set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(),"Account Created!",Toast.LENGTH_SHORT).show();
                            progressDialog.stopLoading();
                            createUserAccount(user.getUserEmail(),binding.inputPassword.getEditText().getText().toString());
                        } else {
                            Toast.makeText(requireContext(),"Create account failed!",Toast.LENGTH_SHORT).show();
                            progressDialog.stopLoading();
                        }
                    });
        }
    }
    private void createUserAccount(String email,String password){
        progressDialog.isLoading();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressDialog.stopLoading();
                startActivity(new Intent(requireActivity(), Login.class));
            } else {
                progressDialog.stopLoading();
                Toast.makeText(requireContext(),"Create user failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}