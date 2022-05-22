package com.example.trash_scan;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProgressDialog {
    private Activity activity;
    private AlertDialog alertDialog;
    public ProgressDialog(Activity activity) {
        this.activity = activity;
    }
    public void isLoading(){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(activity,R.style.ThemeOverlay_App_MaterialAlertDialog);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        materialAlertDialogBuilder.setView(layoutInflater.inflate(R.layout.progress_dialog,null));
        materialAlertDialogBuilder.setCancelable(false);
        alertDialog = materialAlertDialogBuilder.create();
        alertDialog.show();
    }
    public void stopLoading(){
        alertDialog.dismiss();
    }
}
