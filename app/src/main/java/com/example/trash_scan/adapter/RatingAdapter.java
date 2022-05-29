package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.Rating;
import com.example.trash_scan.firebase.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private Context context;
    private List<Rating> ratingList;

    public RatingAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ratings,parent,false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating model = ratingList.get(position);
        holder.setUserInfo(model.getUserID());
        holder.userRating.setRating(model.getRating());
        holder.userDescription.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userProfile;
        private TextView userFullName,userDescription;
        private RatingBar userRating;
        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userFullName = itemView.findViewById(R.id.textUserFullName);
            userDescription = itemView.findViewById(R.id.textDescription);
            userRating = itemView.findViewById(R.id.userRating);
        }
        void setUserInfo(String userID) {
            FirebaseFirestore.getInstance().collection(User.TABLE_NAME)
                    .document(userID)
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (!user.getUserProfile().isEmpty()) {
                                    Picasso.get().load(user.getUserProfile()).into(userProfile);
                                }
                                String fullname = user.getUserFirstName() + " " + user.getUserLastName();
                                userFullName.setText(fullname);
                            }
                        }
                    });
        }
    }
}
