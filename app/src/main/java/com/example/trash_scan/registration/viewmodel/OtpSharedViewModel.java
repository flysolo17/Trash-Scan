package com.example.trash_scan.registration.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OtpSharedViewModel extends ViewModel {
    private final MutableLiveData<CharSequence> phoneNumber = new MutableLiveData<>();
    private final MutableLiveData<CharSequence> verificationCode = new MutableLiveData<>();


    public void setPhoneNumber(CharSequence phone) {
        phoneNumber.setValue(phone);
    }
    public LiveData<CharSequence> getPhoneNumber() {
        return phoneNumber;
    }
    public void setCode(CharSequence code) {
        verificationCode.setValue(code);
    }
    public LiveData<CharSequence> getCode() {
        return verificationCode;
    }


}
