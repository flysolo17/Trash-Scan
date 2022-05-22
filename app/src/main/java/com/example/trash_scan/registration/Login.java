package com.example.trash_scan.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import com.example.trash_scan.junkshop.JunkshopOwner;
import com.example.trash_scan.MainActivity;
import com.example.trash_scan.ProgressDialog;
import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.User;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class Login extends AppCompatActivity{

    private FirebaseAuth firebaseAuth;
    private Validation validation;
    private Button button_login;
    private TextInputLayout input_email,input_password;
    private TextView text_create_account;
    private FirebaseFirestore firestore;
    private String userType;
    private ProgressDialog progressDialog = new ProgressDialog(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        validation = new Validation();
        button_login.setOnClickListener(v -> {
            String email = input_email.getEditText().getText().toString();
            String password = input_password.getEditText().getText().toString();
            if (!validation.validateEmail(input_email) | !validation.validatePassword(input_password)){
                Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email,password);
            }

        });
        text_create_account.setOnClickListener(v -> {
            startActivity(new Intent(this,RegistrationActivity.class));
        });
    }

    //TODO: login script
    private void loginUser(String email,String password) {
            progressDialog.isLoading();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"login Success",Toast.LENGTH_SHORT).show();
                    updateUI(email);
                    progressDialog.stopLoading();
                }
                if (!task.isSuccessful()) {
                    try
                    {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthInvalidUserException invalidEmail)
                    {
                        Log.d(TAG, "onComplete: invalid_email");
                        Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                        progressDialog.stopLoading();
                        // TODO: take your actions!
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                    {
                        Log.d(TAG, "onComplete: wrong_password");
                        Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
                        progressDialog.stopLoading();
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onComplete: " + e.getMessage());
                        progressDialog.stopLoading();
                    }
                }
            });
    }


    //TODO: initialize views
    private void initViews(){
        button_login = findViewById(R.id.button_login);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        text_create_account = findViewById(R.id.text_create_account);
    }


    public void updateUI(String email){
        progressDialog.isLoading();
        firestore.collection(User.TABLE_NAME)
                .whereEqualTo(User.ARG_EMAIL,email)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            if (documentSnapshot.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                                userType = documentSnapshot.getString(User.ARG_USER_TYPE);
                                String userID = documentSnapshot.getId();
                                String fullname  = documentSnapshot.getString(User.ARG_FIRST_NAME) + documentSnapshot.getString(User.ARG_LAST_NAME);
                                if (userType.equals("home owner")){
                                    //intent your home owner interface here
                                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                   intent.putExtra(User.ARG_USER_ID,userID);
                                   intent.putExtra(User.ARG_FIRST_NAME,fullname);
                                   startActivity(intent);
                                   progressDialog.stopLoading();
                                }
                                if (userType.equals("junk shop owner")){
                                    //intent your junk shop owner interface here
                                    Intent intent = new Intent(getApplicationContext(), JunkshopOwner.class);
                                    startActivity(intent);
                                    progressDialog.stopLoading();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                Toast.makeText(getApplicationContext(),"User not found",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
    private static final String TAG = ".Login";

    //this feature is under maintainance
/*    private void loginWithPhone(String input,String password){
        firestore.collection(User.TABLE_NAME)
                .whereEqualTo(User.ARG_PHONE,input)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String email = "";
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        email = documentSnapshot.getString(User.ARG_EMAIL);
                        loginUser(email,password);
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext(),"User not found",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            if (currentUser.getEmail() != null){
                updateUI(currentUser.getEmail());
            }
        }
    }
}