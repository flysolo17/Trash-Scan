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
import com.example.trash_scan.registration.Validation;
import com.example.trash_scan.registration.viewmodel.OtpSharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class CreateUserAccountFragment extends Fragment {


    private FragmentCreateUserAccountBinding binding;

    private FirebaseFirestore firebaseFirestore;
    String userType = "home owner";
    ProgressDialog progressDialog;
    private Validation validation;
    private void init() {
        validation = new Validation();
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
        init();
        progressDialog = new ProgressDialog(getActivity());
        firebaseFirestore = FirebaseFirestore.getInstance();
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
            String firstname = binding.inputFirstName.getEditText().getText().toString();
            String lastname = binding.inputLastName.getEditText().getText().toString();
            String address = binding.inputAddress.getEditText().getText().toString();
            String phone = binding.inputPhoneNumber.getEditText().getText().toString();
            String email = binding.inputEmail.getEditText().getText().toString();
            String password = binding.inputPassword.getEditText().getText().toString();
            String confirmPassword = binding.inputConfirmPassword.getEditText().getText().toString();
            if (firstname.isEmpty()) {
                binding.inputFirstName.setError("Enter Firstname");
            } else if (lastname.isEmpty()){
                binding.inputLastName.setError("Enter Firstname");
            } else if (!validation.validateEmail(binding.inputEmail) ||
                    !validation.validatePhoneNumber(binding.inputPhoneNumber) ||
                    !validation.validatePassword(binding.inputPassword) || !validation.validatePassword(binding.inputConfirmPassword)){
                return;
            } else if (!password.equals(confirmPassword)){
                binding.inputConfirmPassword.setError("Password don't match");
            } else {
                createUserAccount(email,password,firstname,lastname,address,phone,userType,"");
            }
        });
    }

    private void createAccount(User user){
            progressDialog.isLoading();
            firebaseFirestore.collection(User.TABLE_NAME).document(user.getUserID()).set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(),"Account Created!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireActivity(), Login.class));

                        } else {
                            Toast.makeText(requireContext(),"Create account failed!",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.stopLoading();
                    });
    }
    private void createUserAccount(String email,String password,String firstname,String lastname,String address,String phone_number,String userType,String profile){
        progressDialog.isLoading();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser currentUser = task.getResult().getUser();
                if (currentUser != null) {
                    User user = new User(currentUser.getUid(),profile,firstname,lastname,address,email,phone_number,userType);
                    createAccount(user);
                }
                progressDialog.stopLoading();

            } else {
                progressDialog.stopLoading();
                Toast.makeText(requireContext(),"Create user failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}