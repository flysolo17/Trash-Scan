package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JunkShopsWasteAdapter extends RecyclerView.Adapter<JunkShopsWasteAdapter.JunkShopWasteViewHolder> {
    private Context context;
    private List<Recycables> recyclablesList;

    public JunkShopsWasteAdapter(Context context, List<Recycables> recyclablesList) {
        this.context = context;
        this.recyclablesList = recyclablesList;
    }

    @NonNull
    @Override
    public JunkShopWasteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_junkshop_waste,parent,false);
        return new JunkShopWasteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JunkShopWasteViewHolder holder, int position) {
        Recycables recycables = recyclablesList.get(position);
        if (!recycables.getRecycalbleImage().isEmpty()){
            Picasso.get().load(recycables.getRecycalbleImage()).into(holder.imageWaste);
        }
        holder.textWasteName.setText(recycables.getRecycableItemName());
        holder.textWastePrice.setText(String.valueOf(recycables.getRecycablePrice()));
        holder.getjunkshopID(recycables.getJunkshopID());
    }

    @Override
    public int getItemCount() {
        return recyclablesList.size();
    }

    public static class JunkShopWasteViewHolder extends RecyclerView.ViewHolder {
        ImageView imageWaste;
        TextView textWasteOwner,textWasteName,textWastePrice;

        public JunkShopWasteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWaste = itemView.findViewById(R.id.wasteImage);
            textWasteOwner = itemView.findViewById(R.id.textJunkshopOwner);
            textWasteName = itemView.findViewById(R.id.textWasteName);
            textWastePrice = itemView.findViewById(R.id.textWastePrice);

        }
        void getjunkshopID(String junkshopID) {
            FirebaseFirestore.getInstance().collection(User.TABLE_NAME)
                    .document(junkshopID)
                    .get()
                    .addOnSuccessListener( documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                textWasteOwner.setText(user.getUserFirstName() + " " + user.getUserLastName());
                            }
                        }
                    });
        }
    }
}
