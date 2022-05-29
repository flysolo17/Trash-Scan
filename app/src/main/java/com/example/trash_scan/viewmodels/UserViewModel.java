package com.example.trash_scan.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trash_scan.firebase.models.User;

public class UserViewModel extends ViewModel {
    MutableLiveData<User> selected = new MutableLiveData<>();

    public void setUser(User user){
       selected.setValue(user);
    }
    public LiveData<User> getUser(){
        return selected;
    }
}
