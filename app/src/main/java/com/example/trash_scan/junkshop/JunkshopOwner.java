package com.example.trash_scan.junkshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.ActivityJunkshopOwnerBinding;
import com.example.trash_scan.firebase.models.Messages;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.Login;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class JunkshopOwner extends AppCompatActivity {
    private ActivityJunkshopOwnerBinding binding;
    private FirebaseFirestore firestore;
    private NavController navController;
    private List<User> userList;
    private List<String> userIDList;
    int count = 0;
    private void init(){
        userList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJunkshopOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();


        setupNav();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserIDs(currentUser);



    }

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
    private void getUserIDs(FirebaseUser currentUser) {
        userIDList = new ArrayList<>();
        firestore.collection("Messages").addSnapshotListener((value, error) -> {
            userIDList.clear();
            if (error != null) {
                Log.d(".ChatsFragment",error.getMessage());
            }
            if (value != null) {
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    if (documentSnapshot != null) {
                        Messages messages = documentSnapshot.toObject(Messages.class);
                        if (messages.getSenderID().equals(currentUser.getUid())) {
                            userIDList.add(messages.getReceiverID());
                        }
                        if (messages.getReceiverID().equals(currentUser.getUid())) {
                            userIDList.add(messages.getSenderID());
                        }
                    }
                }
                getUserChats(currentUser.getUid());
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void getUserChats(String myID) {
        userList.clear();
        firestore.collection(User.TABLE_NAME).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if ( task.getResult() != null) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        User user = snapshot.toObject(User.class);
                        for (String id : userIDList) {
                            if (user != null && user.getUserID().equals(id)) {
                                if (!userList.contains(user)) {
                                    userList.add(user);
                                }
                            }
                        }
                    }
                    for (User users:
                         userList) {
                        getLastMessage(myID,users.getUserID());
                    }
                }
            }
        });
    }

    private void getLastMessage(String myID , String otherUserID) {
        count = 0;
        List<Messages>messagesList = new ArrayList<>();
        firestore.collection("Messages").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            messagesList.clear();
            if (error != null) {
                Log.w("MessagingFragment", "Listen failed.", error);
                return;
            }
            if (value != null) {
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    if (documentSnapshot != null) {
                        Messages messages = documentSnapshot.toObject(Messages.class);
                        if (messages.getSenderID().equals(myID) && messages.getReceiverID().equals(otherUserID) ||
                                messages.getSenderID().equals(otherUserID) && messages.getReceiverID().equals(myID)) {
                            messagesList.add(messages);
                        }
                    }
                }
                if (!messagesList.get(messagesList.size() -1).getSenderID().equals(myID)) {
                    count+=1;
                }
                setBadgeCount(count);
            }
        });
    }

    private void hideBottomNav() {
        binding.bottomNavigation.setVisibility(View.GONE);
    }
    private void setBadgeCount(int count) {
        if (count != 0) {
            BadgeDrawable badgeDrawable= binding.bottomNavigation.getOrCreateBadge(R.id.nav_messages);
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(count);
        }

    }
}