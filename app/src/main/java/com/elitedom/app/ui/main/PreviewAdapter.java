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

import java.util.ArrayList;

class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private ArrayList<PreviewCard> mTopicsData;
    private Context mContext;

    PreviewAdapter(Context context, ArrayList<PreviewCard> sportsData) {
        this.mTopicsData = sportsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.post_preview, parent, false)) {
            @Override
            public void onClick(View v) {
            }
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
                    Intent intent = new Intent(v.getContext(), PostView.class);
                    intent.putExtra("title", mTitleText.getText().toString());
                    intent.putExtra("subtext", mInfoText.getText().toString());
                    intent.putExtra("image", mPostImage.getContentDescription().toString());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, mCard, "post_expansion");
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
            Intent intent = new Intent(v.getContext(), PostView.class);
            String transitionName = "post_expansion";
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, mCard, transitionName);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
        }
    }
}