package com.example.trash_scan.junkshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.ActivityJunkshopOwnerBinding;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.Login;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class JunkshopOwner extends AppCompatActivity {
    private ActivityJunkshopOwnerBinding binding;
    private FirebaseFirestore firestore;
    private NavController navController;
    private void init(){
        firestore = FirebaseFirestore.getInstance();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJunkshopOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
/*        String userID = getIntent().getStringExtra(User.ARG_USER_ID);
        if (userID != null) {
            getUserInfo(userID);
        }
        binding.buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
        });

        binding.buttonMessages.setOnClickListener(view -> {
            startActivity(new Intent(this,JunkShopOwnerMessages.class));
        });*/
 ;

        setupNav();




    }
/*    private void getUserInfo(String userID){
        firestore.collection(User.TABLE_NAME).document(userID).get().addOnSuccessListener(documentSnapshot -> {
           if (documentSnapshot.exists()) {
               User user = documentSnapshot.toObject(User.class);
               if (user != null) {
                   bindViews(user);
               }
           }
        });
    }

    private void bindViews(User user) {
        if (!user.getUserProfile().isEmpty()) {
            Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
        }
        binding.textUserFullname.setText(user.getUserFirstName() + " " + user.getUserLastName());
        binding.textCompleteAddress.setText(user.getUserAddress());

    }*/
    private void setupNav() {
        navController = Navigation.findNavController(this,R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_home) {
                showBottomNav();
            } else if (destination.getId() == R.id.nav_messages) {
                showBottomNav();
            }
            else if (destination.getId() == R.id.nav_profile) {
                showBottomNav();
            } else {
                hideBottomNav();
            }
        });

    }

    private void showBottomNav() {
        binding.bottomNavigation.setVisibility(View.VISIBLE);

    }

    private void hideBottomNav() {
        binding.bottomNavigation.setVisibility(View.GONE);

    }
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }
}