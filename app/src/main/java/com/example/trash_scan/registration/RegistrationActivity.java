package com.example.trash_scan.registration;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.trash_scan.ProgressDialog;
import com.example.trash_scan.R;
import com.example.trash_scan.databinding.ActivityRegistrationBinding;
import com.example.trash_scan.dialogs.AddressFragment;
import com.example.trash_scan.firebase.models.Address;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.viewmodels.AddressViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;

    private FirebaseFirestore firebaseFirestore;
    String userType = "home owner";
    ProgressDialog progressDialog;
    private Validation validation;
    private AddressViewModel addressViewModel;
    private Address address;
    private void init() {
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        firebaseFirestore = FirebaseFirestore.getInstance();
        validation = new Validation();
        progressDialog = new ProgressDialog(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


        binding.toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonHomeOwner) {
                    userType = "home owner";
                } else if (checkedId == R.id.buttonJunkShopOwner) {
                    userType = "junk shop owner";
                }
            } else if (group.getCheckedButtonId() == View.NO_ID) {
                userType = "home owner";
            } else {
                userType = "home owner";
            }
        });

        binding.buttonCreateAccount.setOnClickListener(v -> {
            String firstname = binding.inputFirstName.getEditText().getText().toString();
            String lastname = binding.inputLastName.getEditText().getText().toString();
            String address = binding.textCompleteAddress.getText().toString();
            String phone = binding.inputPhoneNumber.getEditText().getText().toString();
            String email = binding.inputEmail.getEditText().getText().toString();
            String password = binding.inputPassword.getEditText().getText().toString();
            String confirmPassword = binding.inputConfirmPassword.getEditText().getText().toString();
            if (firstname.isEmpty()) {
                binding.inputFirstName.setError("Enter Firstname");
            } else if (lastname.isEmpty()) {
                binding.inputLastName.setError("Enter Lastname");
            } else if (!validation.validateEmail(binding.inputEmail) ||
                    !validation.validatePhoneNumber(binding.inputPhoneNumber) ||
                    !validation.validatePassword(binding.inputPassword) || !validation.validatePassword(binding.inputConfirmPassword)) {
                return;
            } else if (address.isEmpty()) {
                Toast.makeText(binding.getRoot().getContext(),"Enter Address",Toast.LENGTH_SHORT).show();
            }
            else if (!password.equals(confirmPassword)) {
                binding.inputConfirmPassword.setError("Password don't match");
            } else {
                createUserAccount(email, password, firstname, lastname, address, phone, userType, "");
            }
        });
        binding.edtAddress.setOnClickListener(v -> {
            AddressFragment addressFragment = new AddressFragment();
            if (!addressFragment.isAdded()) {
                addressFragment.show(getSupportFragmentManager(), "AddressFragment");
            }
        });
    }
    private void createAccount(User user){
        progressDialog.isLoading();
        firebaseFirestore.collection(User.TABLE_NAME).document(user.getUserID()).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,"Account Created!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Login.class));

                    } else {
                        Toast.makeText(this,"Create account failed!",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this,"Create user failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressViewModel.getAddress().observe(this, address -> {
            if (address != null) {
                this.address = address;
                String completeAddress = address.getBuildingHouseNo() + ", " + address.getBarangay() + ", " + address.getCity()
                        + ", " + address.getProvince() + ", " + address.getZipcode();
                binding.textCompleteAddress.setText(completeAddress);
            }

        });
    }
}