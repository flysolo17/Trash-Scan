package com.example.trash_scan.firebase;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.example.trash_scan.notification.Utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseOffline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    }
}
