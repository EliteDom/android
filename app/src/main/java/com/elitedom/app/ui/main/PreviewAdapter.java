package com.elitedom.app.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.elitedom.app.ui.profile.user_profile_view;

import java.util.ArrayList;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private ArrayList<PreviewCard> mTopicsData;
    private Context mContext;
    private String transitionType;
    private int intentID;

    public PreviewAdapter(Context context, ArrayList<PreviewCard> topicData, String transitionType, int intentID) {
        this.mTopicsData = topicData;
        this.mContext = context;
        this.transitionType = transitionType;
        this.intentID = intentID;
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

    public void itemasa() {
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
                    Intent intent;
                    ActivityOptionsCompat options;
                    if (intentID == 1) {
                        intent = new Intent(v.getContext(), PostView.class);
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, mCard, transitionType);
                    }
                    else {
                        intent = new Intent(v.getContext(), user_profile_view.class);
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, itemView.findViewById(R.id.user_profile_layout), transitionType);
                    }
                    intent.putExtra("title", mTitleText.getText().toString());
                    intent.putExtra("subtext", mInfoText.getText().toString());
                    intent.putExtra("image", mPostImage.getContentDescription().toString());
                    ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
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
            Intent intent;
            if (intentID == 1) intent = new Intent(v.getContext(), PostView.class);
            else intent = new Intent(v.getContext(), user_profile_view.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, itemView.findViewById(R.id.user_profile_layout), transitionType);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }
    }
}