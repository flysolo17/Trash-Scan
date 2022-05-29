package com.example.trash_scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trash_scan.R;
import com.example.trash_scan.firebase.models.Messages;
import com.example.trash_scan.firebase.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int SEND_MESSAGE = 0;
    private int RECEIVED_MESSAGE = 1;
    private Context context;
    private List<Messages> messagesList;

    public MessagesAdapter(Context context, List<Messages> messagesList){
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SEND_MESSAGE) {
            return new MessageSentViewHolder(LayoutInflater.from(context).inflate(R.layout.row_send_messages, parent, false));
        } else{
            return new MessageReceivedViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_messages, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);

        if (holder.getItemViewType() == SEND_MESSAGE) {
            MessageSentViewHolder sentViewHolder = (MessageSentViewHolder) holder;
            sentViewHolder.message.setText(messages.getMessage());
            sentViewHolder.timestamp.setText(timestampToDate(messages.getTimestamp()));
        } else  {
            MessageReceivedViewHolder receivedViewHolder = (MessageReceivedViewHolder) holder;
            receivedViewHolder.message.setText(messages.getMessage());
            receivedViewHolder.timestamp.setText(timestampToDate(messages.getTimestamp()));
        }
    }


    @Override
    public int getItemViewType(int position) {
        String senderID = messagesList.get(position).getSenderID();
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(senderID)) {
           return SEND_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MessageSentViewHolder extends RecyclerView.ViewHolder {
        TextView message,timestamp;
        public MessageSentViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    static class MessageReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView message,timestamp;
        public MessageReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }


    public String timestampToDate(long timestamp){
        String pattern = "EE MMM dd, HH:mm aa";
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
