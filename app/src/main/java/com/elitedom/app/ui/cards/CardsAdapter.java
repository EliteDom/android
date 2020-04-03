package com.elitedom.app.ui.cards;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elitedom.app.R;

import java.util.ArrayList;

class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder>  {

    private ArrayList<Cards> mTopicsData;
    private Context mContext;

    CardsAdapter(Context context, ArrayList<Cards> sportsData) {
        this.mTopicsData = sportsData;
        this.mContext = context;
    }

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.list_topics, parent, false));
    }

    @Override
    public void onBindViewHolder(CardsAdapter.ViewHolder holder,
                                 int position) {
        Cards currentSport = mTopicsData.get(position);

        holder.bindTo(currentSport);
    }

    @Override
    public int getItemCount() {
        return mTopicsData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleText;
        private TextView mInfoText;
        private ImageView mTopicImage;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mTopicImage = itemView.findViewById(R.id.sportsImage);
            itemView.setOnClickListener(this);
        }

        void bindTo(Cards currentTopic){
            mTitleText.setText(currentTopic.getTitle());
            mInfoText.setText(currentTopic.getInfo());
            Glide.with(mContext).load(currentTopic.getImageResource()).into(mTopicImage);
        }

        @Override
        public void onClick(View view) {
            if(mInfoText.getVisibility() == View.VISIBLE)
            mInfoText.setVisibility(View.GONE);
            else
            mInfoText.setVisibility(View.VISIBLE);
        }
    }
}