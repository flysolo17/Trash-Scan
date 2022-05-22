package com.example.trash_scan.violation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.trash_scan.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {
    private Context context;
    private List<ViolationsAct> violationsActs;
    private ViolationAdapterLister violationAdapterListener;
    public interface ViolationAdapterLister {
        void onContainerClick(int postion);
    }

    public void setMyAdapterListener(ViolationAdapterLister myAdapterListener) {
        this.violationAdapterListener = myAdapterListener;
    }

    public ViolationAdapter( Context context,List<ViolationsAct> violationsActs,ViolationAdapterLister violationAdapterListener){
        this.context = context;
        this.violationsActs = violationsActs;
        this.violationAdapterListener = violationAdapterListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_violation,parent,false);
        return new ViolationViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViolationAdapter.ViolationViewHolder holder, int position) {
        holder.text_title.setText(violationsActs.get(position).getTitle());
        holder.card_violation.setOnClickListener(v -> {
            violationAdapterListener.onContainerClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return violationsActs.size();
    }


    public class ViolationViewHolder extends RecyclerView.ViewHolder {
        TextView text_title;
        MaterialCardView card_violation;
        public ViolationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            card_violation = itemView.findViewById(R.id.card_violation);
        }
    }
}
