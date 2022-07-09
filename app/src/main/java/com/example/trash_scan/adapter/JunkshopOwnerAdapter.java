package com.example.trash_scan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.Messages;
import com.example.trash_scan.firebase.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class JunkshopOwnerAdapter extends FirestoreRecyclerAdapter<User,JunkshopOwnerAdapter.JunkShopOwnerViewHolder> {
    public interface OnJunkShopClick {
        void onJunkShopOwnerClick(int position);
    }
    private final Context context;
    private FirestoreRecyclerOptions<User> options;
    private final OnJunkShopClick onJunkShopClick;
    public JunkshopOwnerAdapter(Context context,@NonNull FirestoreRecyclerOptions<User> options,OnJunkShopClick onJunkShopClick) {
        super(options);
        this.context = context;
        this.options = options;
        this.onJunkShopClick = onJunkShopClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull JunkShopOwnerViewHolder holder, int position, @NonNull User model) {
        holder.textOwnerName.setText(model.getUserFirstName() + " " + model.getUserLastName());

        holder.itemView.setOnClickListener(v -> {
            onJunkShopClick.onJunkShopOwnerClick(position);
        });
        holder.getLastMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),model.getUserID());
    }

    @NonNull
    @Override
    public JunkShopOwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_chats,parent,false);
        return new JunkShopOwnerViewHolder(view);
    }
    public static class JunkShopOwnerViewHolder  extends RecyclerView.ViewHolder {
        TextView textOwnerName,textLastMessage,textTime;
        private FirebaseFirestore firestore;
        public JunkShopOwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            textOwnerName = itemView.findViewById(R.id.junkShopOwnerName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            firestore =FirebaseFirestore.getInstance();
            textTime= itemView.findViewById(R.id.textTime);
        }
        private void getLastMessage(String myID , String otherUserID) {
            List<Messages> messagesList = new ArrayList<>();
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
                    if (messagesList.get(messagesList.size() -1).getSenderID().equals(myID)) {
                        textLastMessage.setText("You: "+messagesList.get(messagesList.size() -1).getMessage());
                    } else {
                        textLastMessage.setTextColor(Color.BLACK);
                        textLastMessage.setText(messagesList.get(messagesList.size() -1).getMessage());
                    }
                    textTime.setText(timestampToTime(messagesList.get(messagesList.size() -1).getTimestamp()));
                }
            });
        }
        private String timestampToTime(long timestamp) {
            Date date = new Date(timestamp);
            Format format = new SimpleDateFormat("HH:mm aa");
            return format.format(date);
        }
    }
}
