package com.example.trash_scan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.adapter.DestinationAdapter;
import com.example.trash_scan.appfeatures.TrashActivity;
import com.example.trash_scan.appfeatures.tips;
import com.example.trash_scan.databinding.FragmentHomeBinding;
import com.example.trash_scan.firebase.models.Destinations;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore firestore;
    private DestinationAdapter destinationAdapter;
    private User user = null;
    private UserViewModel userViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        firestore = FirebaseFirestore.getInstance();
        destinationAdapter = new DestinationAdapter(getAllDestinations());
        binding.cardViewTrash.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_nav_home_to_trashActivity));

        binding.cardViewNotif.setOnClickListener(v ->  Navigation.findNavController(view).navigate(R.id.action_nav_home_to_tips));
        binding.cardViewViolation.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_nav_home_to_violation));

        binding.cardJunkshops.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_junkShopsFragment);
        });
        binding.cardViewProfile.setOnClickListener(v -> {
            if (user != null){
                userViewModel.setUser(user);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_profileFragment);
            }

        });
        binding.recyclerviewDestinations.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerviewDestinations.setAdapter(destinationAdapter);

    }
    private void getUserInfo(String userID){
        firestore.collection(User.TABLE_NAME).document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    this.user = user;
                    if (!user.getUserProfile().isEmpty()) {
                        Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
                    }
                    String fullname = user.getUserFirstName() + " " + user.getUserLastName();
                    binding.userFullname.setText(fullname);
                    binding.userEmail.setText(user.getUserEmail());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfo(MainActivity.userID);
        destinationAdapter.startListening();
    }
    private FirestoreRecyclerOptions<Destinations> getAllDestinations(){
        Query query = firestore.collection("Destinations") .orderBy(
                "timestamp",
                Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Destinations> build = new FirestoreRecyclerOptions.Builder<Destinations>()
                .setQuery(query,Destinations.class)
                .build();
        return build;
    }
}