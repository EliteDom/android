package com.elitedom.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

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
        private CardView mCard;

        ViewHolder(final View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.title);
            mCard = itemView.findViewById(R.id.cardview);
            itemView.setOnClickListener(this);
        }

        void bindTo(PreviewCard currentTopic) {
            mTitleText.setText(currentTopic.getTitle());
//            mInfoText.setText(currentTopic.getSubtext());
        }

        @Override
        public void onClick(View v) {
/*
            Intent intent = new Intent(v.getContext(), PostView.class);
            String transitionName = "post_expansion";
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mCard, transitionName);
            ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
*/
        }
    }
}