package com.example.trash_scan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.adapter.DestinationAdapter;
import com.example.trash_scan.appfeatures.TrackLocation;
import com.example.trash_scan.appfeatures.TrashActivity;
import com.example.trash_scan.appfeatures.Violation;
import com.example.trash_scan.appfeatures.tips;
import com.example.trash_scan.databinding.FragmentHomeBinding;
import com.example.trash_scan.firebase.models.Destinations;
import com.example.trash_scan.firebase.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private String userFirstname = null;
    private FirebaseFirestore firestore;
    private DestinationAdapter destinationAdapter;
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
        firestore = FirebaseFirestore.getInstance();
        destinationAdapter = new DestinationAdapter(getAllDestinations());
        binding.cardViewTrash.setOnClickListener(v -> startActivity(new Intent(getActivity(), TrashActivity.class)));
        binding.cardViewTrack.setOnClickListener(v -> startActivity(new Intent(getActivity(), TrackLocation.class)));
        binding.cardViewNotif.setOnClickListener(v -> startActivity(new Intent(getActivity(), tips.class)));
        binding.cardViewViolation.setOnClickListener(v -> startActivity(new Intent(getActivity(), Violation.class)));
        binding.userFirstName.setOnClickListener(v -> {
            Toast.makeText(requireContext(),MainActivity.userID,Toast.LENGTH_LONG).show();
        });
        binding.cardJunkshops.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_to_junkShopsFragment);
        });
        binding.recyclerviewDestinations.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerviewDestinations.setAdapter(destinationAdapter);
    }
    private void getUserInfo(String userID){
        firestore.collection(User.TABLE_NAME).document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userFirstname = document.getString(User.ARG_FIRST_NAME);
                } else {
                    userFirstname = "No Name";
                }
                binding.userFirstName.setText(userFirstname);
            } else {
                userFirstname = "No Name";
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