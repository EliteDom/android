package com.elitedom.app.ui.messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_MULTIPLE_SENT = 3;
    private static final int VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED = 4;
    private FirebaseFirestore mDatabase;
    private ArrayList<Message> messages;
    private Context mContext;

    MessageAdapter(Context context, ArrayList<Message> messages) {
        this.messages = messages;
        this.mContext = context;
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_MULTIPLE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_multiple_sent, parent, false);
            return new SentMultipleViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_multiple_received, parent, false);
            return new ReceivedMultipleViewHolder(view);
        } else //noinspection ConstantConditions
            return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {
        Message currentMessage = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentViewHolder) holder).bindTo(currentMessage);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedViewHolder) holder).bindTo(currentMessage);
                break;
            case VIEW_TYPE_MESSAGE_MULTIPLE_SENT:
                ((SentMultipleViewHolder) holder).bindTo(currentMessage);
                break;
            case VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED:
                ((ReceivedMultipleViewHolder) holder).bindTo(currentMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSender() == null)
            if (message.getMultipleFlag() == 1) return VIEW_TYPE_MESSAGE_SENT;
            else return VIEW_TYPE_MESSAGE_MULTIPLE_SENT;
        else if (message.getMultipleFlag() == 1) return VIEW_TYPE_MESSAGE_RECEIVED;
        return VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED;
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timestamp;

        SentViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.text_message_body);
            timestamp = itemView.findViewById(R.id.text_message_time);
        }

        void bindTo(Message currentMessage) {
            message.setText(currentMessage.getMessage());
            timestamp.setText(currentMessage.getTimestamp());
        }
    }

    static class SentMultipleViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timestamp;

        SentMultipleViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.text_message_body);
            timestamp = itemView.findViewById(R.id.text_message_time);
        }

        void bindTo(Message currentMessage) {
            message.setText(currentMessage.getMessage());
            timestamp.setText(currentMessage.getTimestamp());
        }
    }

    static class ReceivedMultipleViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timestamp;

        ReceivedMultipleViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.text_message_body);
            timestamp = itemView.findViewById(R.id.text_message_time);
        }

        void bindTo(Message currentMessage) {
            message.setText(currentMessage.getMessage());
            timestamp.setText(currentMessage.getTimestamp());
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {

        private TextView message, sender, timestamp;
        private ImageView image;

        ReceivedViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.text_message_body);
            timestamp = itemView.findViewById(R.id.text_message_time);
            sender = itemView.findViewById(R.id.text_message_name);
            image = itemView.findViewById(R.id.image_message_profile);
            image.setClipToOutline(true);
        }

        void bindTo(Message currentMessage) {
            message.setText(currentMessage.getMessage());
            Glide.with(mContext)
                    .load(currentMessage.getImageResource())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
            image.setContentDescription(currentMessage.getImageResource().toString());
            mDatabase.collection("users").document(currentMessage.getSender())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                sender.setText(document.get("firstName") + " " + document.get("lastName"));
                            }
                        }
                    });
            timestamp.setText(currentMessage.getTimestamp());
        }
    }
}
