package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.Recycables;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycableAdapter extends RecyclerView.Adapter<RecycableAdapter.RecycableViewHolder> {
    private Context context;
    private List<Recycables> recycablesList;
    private OnRecycableClick onRecycableClick;
    public interface OnRecycableClick {
        void onRecycableClick(int position);
    }
    public RecycableAdapter(Context context, List<Recycables> recycablesList, OnRecycableClick onRecycableClick) {
        this.context = context;
        this.recycablesList = recycablesList;
        this.onRecycableClick = onRecycableClick;
    }

    @NonNull
    @Override
    public RecycableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recycables,parent,false);
        return new RecycableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycableViewHolder holder, int position) {
        Recycables recycables = recycablesList.get(position);
        holder.textRecycableItemName.setText(recycables.getRecycableItemName());
        holder.textItemPrice.setText(String.valueOf(recycables.getRecycablePrice()));
        holder.textRecycableInformation.setText(recycables.getRecycableInformation());
        if (!recycables.getRecycalbleImage().isEmpty()) {
            Picasso.get().load(recycables.getRecycalbleImage()).into(holder.recycableImage);
        }
        holder.itemView.setOnClickListener(v-> {
            onRecycableClick.onRecycableClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return recycablesList.size();
    }

    public static class RecycableViewHolder  extends RecyclerView.ViewHolder{
        TextView textRecycableItemName,textRecycableInformation,textItemPrice;
        ImageView recycableImage;
        public RecycableViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecycableItemName = itemView.findViewById(R.id.itemName);
            textItemPrice = itemView.findViewById(R.id.itemPrice);
            textRecycableInformation = itemView.findViewById(R.id.recycableInformation);
            recycableImage = itemView.findViewById(R.id.recycableImage);
        }
    }
}
