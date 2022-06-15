package com.example.trash_scan.junkshop.bottomnav;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.RatingAdapter;
import com.example.trash_scan.adapter.RecycableAdapter;
import com.example.trash_scan.databinding.FragmentJunkShopProfileBinding;
import com.example.trash_scan.firebase.models.Rating;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.ChangePasswordFragment;
import com.example.trash_scan.registration.Login;
import com.example.trash_scan.viewmodels.UserViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JunkShopProfile extends Fragment {

    private static final String TAG = ".JunkShopProfile";
    private FragmentJunkShopProfileBinding binding;
    private FirebaseFirestore firestore;
    private User user = null;
    private UserViewModel userViewModel;
    private List<Rating> ratingList;
    private List<Recycables> recycablesList;
    private RatingAdapter ratingAdapter;

    private void init() {
        binding.recyclerviewReviews.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        firestore = FirebaseFirestore.getInstance();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJunkShopProfileBinding.inflate(inflater,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), Login.class));
        } );

        binding.buttonUpdateProfile.setOnClickListener(view1 -> {
            userViewModel.setUser(user);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_profile_to_updateProfile);
        });
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getRatings(id);
        getRecycables(id);
        binding.buttonChangePassword.setOnClickListener(v -> {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            if (!changePasswordFragment.isAdded()){
                changePasswordFragment.show(getParentFragmentManager(),"ChangePassword");
            }
        });

    }

    private void getUserInfo(String myID) {
        firestore.collection(User.TABLE_NAME).document(myID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        this.user = documentSnapshot.toObject(User.class);
                        bindViews(this.user);
                    } else {
                        Toast.makeText(binding.getRoot().getContext(),"no user",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindViews(User user) {
        if (user != null) {
            if (!user.getUserProfile().isEmpty()) {
                Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
            }
            String fullname = user.getUserFirstName() + " " + user.getUserLastName();
            binding.textUserFullname.setText(fullname);
            binding.textCompleteAddress.setText(user.getUserAddress());
            binding.textUserEmail.setText(user.getUserEmail());
        }
    }
    private void getRatings(String junkshopID) {
        ratingList = new ArrayList<>();
        firestore.collection(User.TABLE_NAME)
                .document(junkshopID)
                .collection("Ratings").addSnapshotListener((value, error) -> {
            ratingList.clear();
            if (error != null) {
                Log.d(TAG,"error: " + error.getMessage());
            }
            if (value != null) {
                for (DocumentSnapshot documentSnapshot : value) {
                    Rating rating = documentSnapshot.toObject(Rating.class);
                    if (rating != null) {
                        ratingList.add(rating);
                        binding.textRatingsCount.setText(String.valueOf(getRatingTotal(ratingList)));
                    }
                }
                ratingAdapter = new RatingAdapter(binding.getRoot().getContext(),ratingList);
                binding.recyclerviewReviews.setAdapter(ratingAdapter);
            }
        });
    }
    private float getRatingTotal(List<Rating> ratingList) {
        float total = 0;
        for (Rating rating : ratingList) {
            total += rating.getRating();
        }
        return total;
    }
    private void getRecycables(String myID) {
        recycablesList = new ArrayList<>();
        firestore.collection(User.TABLE_NAME).document(myID)
                .collection("Recycables")
                .addSnapshotListener((value, error) -> {
                    recycablesList.clear();
                    if (error != null) {
                        Log.d(TAG,error.getMessage());
                    }
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value) {
                            if (snapshot != null) {
                                Recycables recycables = snapshot.toObject(Recycables.class);
                                recycablesList.add(recycables);
                            }
                        }
                        binding.textRecycableCount.setText(String.valueOf(recycablesList.size()));
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        getUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}