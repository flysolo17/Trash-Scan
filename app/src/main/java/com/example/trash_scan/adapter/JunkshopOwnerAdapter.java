package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


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
    }

    @NonNull
    @Override
    public JunkShopOwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_chats,parent,false);
        return new JunkShopOwnerViewHolder(view);
    }
    public static class JunkShopOwnerViewHolder  extends RecyclerView.ViewHolder {
        TextView textOwnerName;
        public JunkShopOwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            textOwnerName = itemView.findViewById(R.id.junkShopOwnerName);
        }
    }
}
