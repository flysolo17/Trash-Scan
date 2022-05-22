package com.example.trash_scan.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trash_scan.R;

import org.jetbrains.annotations.NotNull;


public class LogOutDialog extends DialogFragment {
    private static final String TAG = ".LogOutDialog";
    public interface LogOutFeedBack{
        void sendLogOutFeedBack(boolean feedback);
    }
    public LogOutFeedBack logOutFeedBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogOut = view.findViewById(R.id.buttonLogout);
        Button btnCancel = view.findViewById(R.id.buttonCancel);
        btnLogOut.setOnClickListener(v -> {
            logOutFeedBack.sendLogOutFeedBack(true);
            getDialog().dismiss();
        });
        btnCancel.setOnClickListener(v -> {
            getDialog().dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            logOutFeedBack = (LogOutFeedBack) getActivity();
        }catch (ClassCastException e){
            Log.d(TAG,"onAttach: ClassCastException: " + e.getMessage());
        }

    }
}