package com.example.trash_scan.appfeatures;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.RatingAdapter;
import com.example.trash_scan.adapter.RecycableAdapter;
import com.example.trash_scan.databinding.FragmentViewJunkShopProfileBinding;
import com.example.trash_scan.firebase.models.Rating;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewJunkShopProfile extends Fragment implements RecycableAdapter.OnRecycableClick {
    private static final String TAG = ".ViewJunkShopProfile";
    private FragmentViewJunkShopProfileBinding binding;
    private UserViewModel userViewModel;
    private FirebaseFirestore firestore;
    private User junkshop = null;
    private RatingAdapter ratingAdapter;
    private RecycableAdapter recycableAdapter;
    private List<Rating> ratingList;
    private List<Recycables> recycablesList;
    private void init() {
        firestore = FirebaseFirestore.getInstance();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        binding.recyclerviewRatings.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.recyclerviewRecycables.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewJunkShopProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                junkshop = user;
                bindViews(junkshop);
                getRatings(FirebaseAuth.getInstance().getCurrentUser().getUid(),junkshop);
                getRecycables(junkshop.getUserID());
            }
        });
        binding.buttonPost.setOnClickListener(view1 -> {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Rating rating = new Rating(userID,binding.ratingBar.getRating(),binding.inputRatingDescription.getEditText().getText().toString());
            addRating(junkshop,rating);
        });




    }
    private void bindViews(User user) {
        if (!user.getUserProfile().isEmpty()) {
            Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
        }
        String fullname = user.getUserFirstName() + " " + user.getUserLastName();
        binding.textUserFullName.setText(fullname);
        binding.textUserAddress.setText(user.getUserAddress());
    }
    private void addRating(User user,Rating rating) {
        firestore.collection(User.TABLE_NAME).document(user.getUserID())
                .collection("Ratings").document(rating.getUserID())
                .set(rating).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(binding.getRoot().getContext(), "Rated Successfully", Toast.LENGTH_SHORT).show();
                        binding.cardViewRating.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(binding.getRoot().getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getRatings(String myID,User junkshop) {
        ratingList = new ArrayList<>();
        firestore.collection(User.TABLE_NAME)
                .document(junkshop.getUserID())
                .collection("Ratings").addSnapshotListener((value, error) -> {
            ratingList.clear();
            if (error != null) {
                Log.d(TAG,"error: " + error.getMessage());
            }
            if (value != null) {
                for (DocumentSnapshot documentSnapshot : value) {
                    Rating rating = documentSnapshot.toObject(Rating.class);
                    if (rating != null) {
                        if (rating.getUserID().equals(myID)) {
                            binding.cardViewRating.setVisibility(View.GONE);
                        }
                        ratingList.add(rating);
                        binding.textRatings.setText(String.valueOf(getRatingTotal(ratingList)));
                    }
                }
                ratingAdapter = new RatingAdapter(binding.getRoot().getContext(),ratingList);
                binding.recyclerviewRatings.setAdapter(ratingAdapter);
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
                        recycableAdapter = new RecycableAdapter(binding.getRoot().getContext(),recycablesList,this);
                        binding.recyclerviewRecycables.setAdapter(recycableAdapter);
                        binding.textRecycableItems.setText(String.valueOf(recycablesList.size()));
                        if (recycablesList.size() == 0) {
                            binding.textRecycable.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onRecycableClick(int position) {
        Toast.makeText(binding.getRoot().getContext(),recycablesList.get(position).getRecycableItemName(),Toast.LENGTH_SHORT).show();
    }
}