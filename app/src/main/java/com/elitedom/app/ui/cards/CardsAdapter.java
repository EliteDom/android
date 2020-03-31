package com.elitedom.app.ui.cards;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mInfoText;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
        }

        void bindTo(Cards currentSport){
            // Populate the textviews with data.
            mTitleText.setText(currentSport.getTitle());
            mInfoText.setText(currentSport.getInfo());

        }
    }
}