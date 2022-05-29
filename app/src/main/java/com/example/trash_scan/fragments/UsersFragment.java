package com.example.trash_scan.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.UsersAdapter;
import com.example.trash_scan.databinding.FragmentUsersBinding;
import com.example.trash_scan.firebase.models.Destinations;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.Login;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UsersFragment extends Fragment implements UsersAdapter.OnUserClick {
    private FragmentUsersBinding binding;
    private FirebaseFirestore firestore;
    private UsersAdapter adapter;
    private UserViewModel userViewModel;
    private void init(){
        firestore = FirebaseFirestore.getInstance();
        adapter = new UsersAdapter(binding.getRoot().getContext(),getAllUsers(Login.userType),this);
        binding.recyclerviewUsers.setAdapter(adapter);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

    }

    private FirestoreRecyclerOptions<User> getAllUsers(String userType) {
        if (userType.equals("home owner")) {
            Query query = firestore.collection(User.TABLE_NAME).whereEqualTo("userType","junk shop owner");
            FirestoreRecyclerOptions<User> build = new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query,User.class)
                    .build();
            return build;
        } else {
            Query query = firestore.collection(User.TABLE_NAME).whereEqualTo("userType","home owner");
            FirestoreRecyclerOptions<User> build = new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query,User.class)
                    .build();
            return build;
        }
    }

    @Override
    public void onClickUser(int position) {
        userViewModel.setUser(adapter.getSnapshots().get(position));
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_usersFragment_to_messagingFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}