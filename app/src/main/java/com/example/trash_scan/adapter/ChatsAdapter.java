package com.example.trash_scan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ContentView;
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
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private Context context;
    private List<User> userList;
    private OnChatClick onChatClick;

     public interface OnChatClick {
        void onUserClick(int position);
    }
    public ChatsAdapter(Context context,List<User> userList ,OnChatClick onChatClick){
        this.context = context;
        this.userList = userList;
        this.onChatClick = onChatClick;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_chats,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = userList.get(position);
        if (!user.getUserProfile().isEmpty()) {
            Picasso.get().load(user.getUserProfile()).into(holder.userImage);
        }
        holder.textUserName.setText(user.getUserFirstName() + " " + user.getUserLastName());
        holder.itemView.setOnClickListener(view -> onChatClick.onUserClick(position));
        holder.getLastMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),user.getUserID());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView textUserName,textLastMessage,textTime;
        private FirebaseFirestore firestore;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.junkShopProfile);
            textUserName = itemView.findViewById(R.id.junkShopOwnerName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            firestore =FirebaseFirestore.getInstance();
            textTime= itemView.findViewById(R.id.textTime);

        }
        private void getLastMessage(String myID , String otherUserID) {
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
