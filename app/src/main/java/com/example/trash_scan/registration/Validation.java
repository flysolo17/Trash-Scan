package com.example.trash_scan.registration;

import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;

public class Validation {

    public boolean validateEmail(TextInputLayout input_email){
        String email = input_email.getEditText().getText().toString();
        if (email.isEmpty()) {
            input_email.setError("Please enter your email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            input_email.setError("Incorrect email");
            return false;
        }
        else {
            input_email.setError(null);
            return true;
        }
    }
    public boolean validatePassword(TextInputLayout input_password){
        String password = input_password.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            input_password.setError("Please enter your password");
            return false;
        }
        if (password.length() < 7){
            input_password.setError("Invalid Password");
            return false;
        }
        else {
            input_password.setError(null);
            return true;
        }
    }
    public boolean validatePhoneNumber(TextInputLayout input_phone_number){
        String number = input_phone_number.getEditText().getText().toString().trim();
        if (number.isEmpty()) {
            input_phone_number.setError("Please enter your number");
            return false;
        }
        if (!number.startsWith("9")) {
            input_phone_number.setError("Invalid number");
            return false;
        }

        if (number.length() != 10){
            input_phone_number.setError("Invalid number");
            return false;
        }
        else {
            input_phone_number.setError(null);
            return true;
        }
    }




}
