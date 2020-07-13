package com.elitedom.app.ui.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elitedom.app.R;
import com.elitedom.app.ui.dorms.DormProfile;

import java.util.ArrayList;

class ResultAdapter extends RecyclerView.Adapter<com.elitedom.app.ui.search.ResultAdapter.ViewHolder> {

    private ArrayList<Result> mTopicsData;
    private Context mContext;

    ResultAdapter(Context context, ArrayList<Result> topicData) {
        this.mTopicsData = topicData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public com.elitedom.app.ui.search.ResultAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(com.elitedom.app.ui.search.ResultAdapter.ViewHolder holder,
                                 int position) {
        Result currentTopic = mTopicsData.get(position);
        holder.bindTo(currentTopic);
    }

    @Override
    public int getItemCount() {
        return mTopicsData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private CardView mCard;
        private TextView name;

        ViewHolder(final View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.single_card);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            icon.setClipToOutline(true);
        }

        void bindTo(Result currentResult) {
            name.setText(currentResult.getName());
            Glide.with(mContext)
                    .load(currentResult.getImageResource())
                    .into(icon);
            mCard.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DormProfile.class);
                intent.putExtra("dorm", currentResult.getName());
                intent.putExtra("image", currentResult.getImageResource());
                mContext.startActivity(intent);
            });
        }
    }
}
