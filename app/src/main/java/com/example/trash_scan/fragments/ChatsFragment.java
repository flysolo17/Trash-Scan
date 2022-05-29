package com.example.trash_scan.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trash_scan.R;
import com.example.trash_scan.adapter.ChatsAdapter;
import com.example.trash_scan.databinding.FragmentChatsBinding;
import com.example.trash_scan.firebase.models.Messages;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.Login;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements ChatsAdapter.OnChatClick {

    private FragmentChatsBinding binding;
    private ChatsAdapter adapter;
    private FirebaseFirestore firestore;
    private UserViewModel userViewModel;
    private List<User> userList;
    private List<String> userIDList;
    private void  init(){
        userList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        binding.recyclerviewChats.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserIDs(currentUser);
        adapter = new ChatsAdapter(binding.getRoot().getContext(),userList,this);
        binding.recyclerviewChats.setAdapter(adapter);
        binding.buttonChatUser.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).navigate(R.id.action_junkShopsFragment_to_usersFragment));
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
                getUserChats();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getUserChats() {
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
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onUserClick(int position) {
        userViewModel.setUser(userList.get(position));
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_junkShopsFragment_to_messagingFragment);
    }
}