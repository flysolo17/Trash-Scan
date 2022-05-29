package com.example.trash_scan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.trash_scan.databinding.ActivityMainBinding;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.fragments.LogOutDialog;
import com.example.trash_scan.registration.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity  implements LogOutDialog.LogOutFeedBack {
    private AppBarConfiguration mAppBarConfiguration;
    public static String userID;
    private ActivityMainBinding binding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userID = getIntent().getStringExtra(User.ARG_USER_ID);
        //set nav bar
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_contact,R.id.nav_setting,R.id.nav_logout)
                .setOpenableLayout(binding.drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void sendLogOutFeedBack(boolean feedback) {
        if (feedback){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this,"Log Out Successfully! ",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}