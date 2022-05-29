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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends FirestoreRecyclerAdapter<User,UsersAdapter.UsersViewHolder> {

    public interface OnUserClick{
        void onClickUser(int position);
    }
    private Context context;
    private FirestoreRecyclerOptions<User> options;
    private OnUserClick onUserClick;
    public UsersAdapter(Context context,@NonNull FirestoreRecyclerOptions<User> options,OnUserClick onUserClick) {
        super(options);
        this.context = context;
        this.options = options;
        this.onUserClick = onUserClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
        holder.userFullName.setText(model.getUserFirstName() + " " + model.getUserLastName());
        if (!model.getUserProfile().isEmpty()) {
            Picasso.get().load(model.getUserProfile()).into(holder.userProfile);
        }
        holder.itemView.setOnClickListener(v -> onUserClick.onClickUser(position));

    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);
        return new UsersViewHolder(view);
    }

    public class UsersViewHolder  extends RecyclerView.ViewHolder {
        private CircleImageView userProfile;
        private TextView userFullName;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userFullName = itemView.findViewById(R.id.textUserFullName);
            userProfile  = itemView.findViewById(R.id.imageUserProfile);
        }
    }
}
