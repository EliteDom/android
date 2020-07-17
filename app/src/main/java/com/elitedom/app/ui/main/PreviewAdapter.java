package com.elitedom.app.ui.main;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.elitedom.app.ui.profile.ProfilePostView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class PreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CREATOR = 2;
    private static final int VIEW_TYPE_POST = 1;
    private ArrayList<PreviewCard> mTopicsData;
    private FirebaseFirestore mDatabase;
    private View image, name, card;
    private String transitionType;
    private String profileUri;
    private Context mContext;
    private int intentID;

    public PreviewAdapter(Context context, ArrayList<PreviewCard> topicData, String transitionType, int intentID) {
        this.mTopicsData = topicData;
        this.mContext = context;
        this.transitionType = transitionType;
        this.intentID = intentID;
        this.mDatabase = FirebaseFirestore.getInstance();
        this.profileUri = "";
        this.getProfileUri();
    }

    private void getProfileUri() {
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        PreviewAdapter.this.profileUri = (String) document.get("image");
                    }
                });
    }

    public void sendContext(View image, View name, View card) {
        this.image = image;
        this.name = name;
        this.card = card;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_POST) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_post_preview, parent, false);
            return new PostViewHolder(view);
        } else if (viewType == VIEW_TYPE_CREATOR) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_post_creator, parent, false);
            return new CreatorViewHolder(view);
        } else //noinspection ConstantConditions
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {
        PreviewCard currentPost = mTopicsData.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_POST:
                ((PreviewAdapter.PostViewHolder) holder).bindTo(currentPost);
                break;
            case VIEW_TYPE_CREATOR:
                ((PreviewAdapter.CreatorViewHolder) holder).bindTo(currentPost);
        }
    }

    @Override
    public int getItemCount() {
        return mTopicsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        PreviewCard currentPost = mTopicsData.get(position);
        if (currentPost.getTitle() == null) return VIEW_TYPE_CREATOR;
        else return VIEW_TYPE_POST;
    }

    class CreatorViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private EditText editor;
        private CardView mCard;

        CreatorViewHolder(final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_profile);
            editor = itemView.findViewById(R.id.editor);
            mCard = itemView.findViewById(R.id.creator_card);
        }

        void bindTo(PreviewCard currentPost) {
            if (currentPost.getImageResource() != null && currentPost.getImageResource().toString().length() > 0) {
                Glide.with(mContext)
                        .load(currentPost.getImageResource())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image);
                image.setContentDescription(currentPost.getImageResource().toString());
                image.setClipToOutline(true);

                editor.setOnClickListener(this::createPost);
                itemView.setOnClickListener(this::createPost);
            }
        }

        private void createPost(View view) {
            Intent intent = new Intent(mContext, PostCreator.class);
            intent.putExtra("image", image.getContentDescription());
            intent.putExtra("title", editor.getText().toString());
            editor.setText("");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, mCard, transitionType);
            ActivityCompat.startActivity(mContext, intent, options.toBundle());
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText, mInfoText, mAuthor;
        private ImageView mPostImage;
        private CardView mCard;

        PostViewHolder(final View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.post_text);
            mCard = itemView.findViewById(R.id.cardview);
            mPostImage = itemView.findViewById(R.id.postImage);
            mAuthor = itemView.findViewById(R.id.author);
            mPostImage.setClipToOutline(true);
            mDatabase = FirebaseFirestore.getInstance();

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(v -> {
                if (intentID == 1) feedActivity(v);
                else profileActivity(v);
            });
        }

        void bindTo(PreviewCard currentPost) {
            mTitleText.setContentDescription(currentPost.getUid());
            mTitleText.setText(currentPost.getTitle());
            mInfoText.setContentDescription(currentPost.getDorm());
            mInfoText.setText(currentPost.getBody());
            mAuthor.setText(currentPost.getAuthor());
            if (currentPost.getImageResource() != null && currentPost.getImageResource().toString().length() > 0) {
                itemView.findViewById(R.id.postImage).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.post_text).setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(currentPost.getImageResource())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mPostImage);
            }
            else {
                itemView.findViewById(R.id.post_text).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.postImage).setVisibility(View.GONE);
            }
            mPostImage.setContentDescription(currentPost.getImageResource().toString());
        }

        @Override
        public void onClick(View v) {
            if (intentID == 1) feedActivity(v);
            else profileActivity(v);
        }

        private void profileActivity(View v) {
            Intent intent = new Intent(v.getContext(), ProfilePostView.class);
            intent.putExtra("title", mTitleText.getText().toString());
            intent.putExtra("uid", mTitleText.getContentDescription().toString());
            intent.putExtra("dorm", mInfoText.getContentDescription().toString());
            intent.putExtra("subtext", mInfoText.getText().toString());
            intent.putExtra("author", mAuthor.getText().toString());
            intent.putExtra("image", mPostImage.getContentDescription().toString());
            intent.putExtra("profileImage", profileUri);

            Pair<View, String> t1 = Pair.create(image, "image");
            Pair<View, String> t2 = Pair.create(name, "username");
            Pair<View, String> t3 = Pair.create(card, "post_cards");
            Pair<View, String> t4 = Pair.create(mCard, transitionType);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), t1, t2, t3, t4);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }

        private void feedActivity(View v) {
            Intent intent = new Intent(v.getContext(), PostView.class);
            intent.putExtra("title", mTitleText.getText().toString());
            intent.putExtra("uid", mTitleText.getContentDescription().toString());
            intent.putExtra("dorm", mInfoText.getContentDescription().toString());
            intent.putExtra("subtext", mInfoText.getText().toString());
            intent.putExtra("author", mAuthor.getText().toString());
            intent.putExtra("image", mPostImage.getContentDescription().toString());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), mCard, transitionType);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }
    }
}