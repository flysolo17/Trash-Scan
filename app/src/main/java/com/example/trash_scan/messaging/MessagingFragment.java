package com.example.trash_scan.messaging;

import android.app.NotificationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.trash_scan.MainActivity;
import com.example.trash_scan.R;
import com.example.trash_scan.adapter.MessagesAdapter;
import com.example.trash_scan.databinding.FragmentMessagingBinding;
import com.example.trash_scan.firebase.models.Messages;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.notification.Utils;
import com.example.trash_scan.registration.Login;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MessagingFragment extends Fragment {
    private UserViewModel userViewModel;
    private FragmentMessagingBinding binding;
    private FirebaseFirestore firestore;
    private MessagesAdapter adapter;
    private User junkshopOwner = null;
    private List<Messages> messagesList;
    private void init(){

        firestore = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerviewMessages.setLayoutManager(layoutManager);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMessagingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            junkshopOwner = user;
            bindViews(junkshopOwner);
            getMessages(FirebaseAuth.getInstance().getCurrentUser().getUid(),user.getUserID());
        });

        binding.buttonSendMessage.setOnClickListener(view1 -> {
            String senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String message = binding.inputMessage.getText().toString();
            Messages messages = new Messages(senderID, junkshopOwner.getUserID(), message);
            sendMessages(messages);

        });
        binding.buttonVisitProfile.setOnClickListener(v-> {

            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_messagingFragment_to_viewJunkShopProfile);
        });
    }

    private void bindViews(User user) {
        if (user != null) {
            ((MainActivity) requireActivity()).setActionBarTitle(user.getUserFirstName() + " " + user.getUserLastName());
            if (!user.getUserProfile().isEmpty()) {
                Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
            }
            binding.userFullname.setText(user.getUserFirstName() + " " + user.getUserLastName());
        }
    }
    private void sendMessages(Messages messages) {
        firestore.collection("Messages").add(messages)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(),"Message Sent!",Toast.LENGTH_SHORT).show();
                        binding.inputMessage.setText("");
                    } else {
                        Toast.makeText(requireActivity(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getMessages(String myID , String otherUserID) {
        messagesList = new ArrayList<>();
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
                adapter = new MessagesAdapter(binding.getRoot().getContext(),messagesList);
                binding.recyclerviewMessages.setAdapter(adapter);
                if (messagesList.size() == 0) {
                    binding.textNoMessages.setVisibility(View.VISIBLE);
                } else  {
                    binding.textNoMessages.setVisibility(View.GONE);
                }
            }

        });
    }


}