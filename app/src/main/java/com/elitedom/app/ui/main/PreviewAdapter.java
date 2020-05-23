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
import com.elitedom.app.ui.profile.profile_post;
import com.elitedom.app.ui.profile.user_profile;

import java.util.ArrayList;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private ArrayList<PreviewCard> mTopicsData;
    private Context mContext;
    private String transitionType;
    private user_profile profileContext;
    private View image, name, card;
    private int intentID;

    public PreviewAdapter(Context context, ArrayList<PreviewCard> topicData, String transitionType, int intentID) {
        this.mTopicsData = topicData;
        this.mContext = context;
        this.transitionType = transitionType;
        this.intentID = intentID;
    }

    public void sendContext(user_profile profileContext, View image, View name, View card) {
        this.profileContext = profileContext;
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

        private TextView mTitleText, mInfoText;
        private ImageView mPostImage;
        private CardView mCard;

        ViewHolder(final View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.post_text);
            mCard = itemView.findViewById(R.id.cardview);
            mPostImage = itemView.findViewById(R.id.postImage);
            mPostImage.setClipToOutline(true);

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (intentID == 1) feedActivity(v);
                    else profileActivity(v);
                }
            });
        }

        void bindTo(PreviewCard currentTopic) {
            mTitleText.setText(currentTopic.getTitle());
            mInfoText.setText(currentTopic.getSubtext());
            Glide.with(mContext)
                    .load(currentTopic.getImageResource())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mPostImage);
            mPostImage.setContentDescription(currentTopic.getImageResource().toString());
        }

        @Override
        public void onClick(View v) {
            if (intentID == 1) feedActivity(v);
            else profileActivity(v);
        }

        private void profileActivity(View v) {
            @SuppressLint("InflateParams") View postLayout = LayoutInflater.from(mContext).inflate(R.layout.activity_profile_post, null);

            Intent intent = new Intent(v.getContext(), profile_post.class);
            intent.putExtra("title", mTitleText.getText().toString());
            intent.putExtra("subtext", mInfoText.getText().toString());
            intent.putExtra("image", mPostImage.getContentDescription().toString());

            Pair<View, String> t1 = Pair.create(image, "image");
            Pair<View, String> t2 = Pair.create(name, "username");
            Pair<View, String> t3 = Pair.create(card, "post_cards");
//            Pair<View, String> t4 = Pair.create(postLayout.findViewById(R.id.cardview), transitionType);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), t1, t2, t3);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }

        private void feedActivity(View v) {
            Intent intent = new Intent(v.getContext(), PostView.class);
            intent.putExtra("title", mTitleText.getText().toString());
            intent.putExtra("subtext", mInfoText.getText().toString());
            intent.putExtra("image", mPostImage.getContentDescription().toString());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), mCard, transitionType);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }
    }
}
