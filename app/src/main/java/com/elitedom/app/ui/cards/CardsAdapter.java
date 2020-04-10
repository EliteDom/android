package com.elitedom.app.ui.cards;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elitedom.app.R;

import java.util.ArrayList;

import static android.view.View.GONE;

class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText, mInfoText;
        private ImageView mTopicImage;
        private Animation in, out;
        private RelativeLayout mCardLayout;

        ViewHolder(final View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mTopicImage = itemView.findViewById(R.id.topicImage);
            mCardLayout = itemView.findViewById(R.id.singlecardlayout);

            in = AnimationUtils.loadAnimation(mContext, R.anim.cards_subtext_in);
            out = AnimationUtils.loadAnimation(mContext, R.anim.cards_subtext_out);

            in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { mCardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING); }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mCardLayout.setLayoutTransition(null);
                    mInfoText.setVisibility(GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { mCardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING); }
                @Override
                public void onAnimationEnd(Animation animation) { mCardLayout.setLayoutTransition(null); }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            mInfoText.setTranslationY(-170f);
            mInfoText.setVisibility(GONE);
            itemView.setOnClickListener(this);
        }

        void bindTo(Cards currentTopic) {
            mTitleText.setText(currentTopic.getTitle());
            mInfoText.setText(currentTopic.getInfo());
            Glide.with(mContext).load(currentTopic.getImageResource()).into(mTopicImage);
        }

        @Override
        public void onClick(View view) {
            mCardLayout.setLayoutTransition(new LayoutTransition());
            if (mInfoText.getVisibility() != View.VISIBLE) {
                mInfoText.setVisibility(View.VISIBLE);
                mInfoText.startAnimation(out);
            }
            else
            mInfoText.startAnimation(in);
        }
    }
}