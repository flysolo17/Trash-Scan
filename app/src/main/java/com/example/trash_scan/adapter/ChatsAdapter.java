package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

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
        holder.textUserEmail.setText(user.getUserEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView textUserName,textUserEmail;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.junkShopProfile);
            textUserName = itemView.findViewById(R.id.junkShopOwnerName);
            textUserEmail = itemView.findViewById(R.id.userEmail);
        }
    }
}
