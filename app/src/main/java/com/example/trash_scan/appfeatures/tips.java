package com.example.trash_scan.appfeatures;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.ActivityTipsBinding;

public class tips extends Fragment {
    private ActivityTipsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding =ActivityTipsBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }
}