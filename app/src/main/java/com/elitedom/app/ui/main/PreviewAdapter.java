package com.elitedom.app.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private String profileUri;
    private FirebaseFirestore mDatabase;
    private ArrayList<PreviewCard> mTopicsData;
    private Context mContext;
    private String transitionType;
    private View image, name, card;
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
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            PreviewAdapter.this.profileUri = (String) document.get("image");
                        }
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
    public PreviewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.post_preview, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(PreviewAdapter.ViewHolder holder,
                                 int position) {
        PreviewCard currentTopic = mTopicsData.get(position);
        holder.bindTo(currentTopic);
    }

    @Override
    public int getItemCount() {
        return mTopicsData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText, mInfoText, mAuthor;
        private ImageView mPostImage;
        private CardView mCard;

        ViewHolder(final View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.post_text);
            mCard = itemView.findViewById(R.id.cardview);
            mPostImage = itemView.findViewById(R.id.postImage);
            mAuthor = itemView.findViewById(R.id.author);
            mPostImage.setClipToOutline(true);
            mDatabase = FirebaseFirestore.getInstance();

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (intentID == 1) feedActivity(v);
                    else profileActivity(v);
                }
            });
        }

        void bindTo(PreviewCard currentPost) {
            mTitleText.setContentDescription(currentPost.getUid());
            mTitleText.setText(currentPost.getTitle());
            mInfoText.setContentDescription(currentPost.getDorm());
            mInfoText.setText(currentPost.getSubtext());
            mAuthor.setText(currentPost.getAuthor());
            Glide.with(mContext)
                    .load(currentPost.getImageResource())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mPostImage);
            mPostImage.setContentDescription(currentPost.getImageResource().toString());
        }

        @Override
        public void onClick(View v) {
            if (intentID == 1) feedActivity(v);
            else profileActivity(v);
        }

        private void profileActivity(View v) {
            Intent intent = new Intent(v.getContext(), ProfilePostView.class);
            intent.putExtra("author", mAuthor.getText().toString());
            intent.putExtra("title", mTitleText.getText().toString());
            intent.putExtra("subtext", mInfoText.getText().toString());
            intent.putExtra("uid", mTitleText.getContentDescription().toString());
            intent.putExtra("dorm", mInfoText.getContentDescription().toString());
            intent.putExtra("image", mPostImage.getContentDescription().toString());
            intent.putExtra("profileImage", profileUri);

            Pair<View, String> t1 = Pair.create(image, "image");
            Pair<View, String> t2 = Pair.create(name, "username");
            Pair<View, String> t3 = Pair.create(card, "post_cards");
            Pair<View, String> t4 = Pair.create((View) mCard, transitionType);
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
