package com.example.trash_scan.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trash_scan.firebase.models.Address;
import com.example.trash_scan.firebase.models.User;

public class AddressViewModel extends ViewModel {
    MutableLiveData<Address> selected = new MutableLiveData<>();

    public void setAddress(Address address){
        selected.setValue(address);
    }
    public LiveData<Address> getAddress(){
        return selected;
    }
}
