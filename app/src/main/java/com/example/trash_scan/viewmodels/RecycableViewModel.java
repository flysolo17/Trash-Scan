package com.example.trash_scan.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;

public class RecycableViewModel extends ViewModel {
    MutableLiveData<Recycables> selected = new MutableLiveData<>();

    public void setRecycables(Recycables recycables){
        selected.setValue(recycables);
    }
    public LiveData<Recycables> getRecycables(){
        return selected;
    }
}