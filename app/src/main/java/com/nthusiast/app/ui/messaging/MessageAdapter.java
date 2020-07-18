package com.nthusiast.app.ui.messaging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.profile.UserProfile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class MessageAdapter extends Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_MULTIPLE_SENT = 3;
    private static final int VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED = 4;
    private static final int VIEW_TYPE_DATESTAMP = 5;
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
        } else if (viewType == VIEW_TYPE_DATESTAMP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_datestamp, parent, false);
            return new DatestampViewHolder(view);
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
                break;
            case VIEW_TYPE_DATESTAMP:
                ((DatestampViewHolder) holder).bindTo(currentMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getMessage() == null) return VIEW_TYPE_DATESTAMP;
        else if (message.getSender() == null)
            if (message.getMultipleFlag() == 1) return VIEW_TYPE_MESSAGE_SENT;
            else return VIEW_TYPE_MESSAGE_MULTIPLE_SENT;
        else if (message.getMultipleFlag() == 1) return VIEW_TYPE_MESSAGE_RECEIVED;
        return VIEW_TYPE_MESSAGE_MULTIPLE_RECEIVED;
    }

    static class DatestampViewHolder extends RecyclerView.ViewHolder {

        private TextView timestamp;

        DatestampViewHolder(final View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.text_datestamp);
        }

        void bindTo(Message currentMessage) {
            timestamp.setText(currentMessage.getTimestamp());
        }
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
            image.setOnClickListener(v -> {
                Intent profile_intent = new Intent(mContext, UserProfile.class);
                profile_intent.putExtra("auth", sender.getContentDescription());
                mContext.startActivity(profile_intent);
            });
            image.setClipToOutline(true);
        }

        @SuppressLint("SetTextI18n")
        void bindTo(Message currentMessage) {
            message.setText(currentMessage.getMessage());
            Glide.with(mContext)
                    .load(currentMessage.getImageResource())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
            image.setContentDescription(currentMessage.getImageResource().toString());
            mDatabase.collection("users").document(currentMessage.getSender())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            sender.setText(document.get("firstName") + " " + document.get("lastName"));
                            sender.setContentDescription(currentMessage.getSender());
                        }
                    });
            timestamp.setText(currentMessage.getTimestamp());
        }
    }
}
