package com.nthusiast.app.ui.communities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.main.PreviewCard;

import java.util.ArrayList;

import static android.view.View.GONE;

class CardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CARD = 1;
    private static final int VIEW_TYPE_TEXT = 2;
    private ArrayList<String> mTickedNames;
    private ArrayList<Card> mTopicsData;
    private Context mContext;

    CardsAdapter(Context context, ArrayList<Card> topicData) {
        this.mTopicsData = topicData;
        this.mContext = context;
        mTickedNames = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CARD) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_topic_card, parent, false);
            return new CardsAdapter.CardViewHolder(view);
        } else if (viewType == VIEW_TYPE_TEXT) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_label_text, parent, false);
            return new TextViewHolder(view);
        } else //noinspection ConstantConditions
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {
        Card card = mTopicsData.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CARD:
                ((CardsAdapter.CardViewHolder) holder).bindTo(card);
                break;
            case VIEW_TYPE_TEXT:
                ((CardsAdapter.TextViewHolder) holder).bindTo(card);
        }
    }

    @Override
    public int getItemCount() {
        return mTopicsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Card card = mTopicsData.get(position);
        if (card.getInfo() == null) return VIEW_TYPE_TEXT;
        else return VIEW_TYPE_CARD;
    }

    private void updateCardNames(String s, int n) {
        if (n == 1) mTickedNames.add(s);
        else mTickedNames.remove(s);
    }

    ArrayList<String> getCardNames() {
        return mTickedNames;
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView mLabel;

        TextViewHolder(final View itemView) {
            super(itemView);

            mLabel = itemView.findViewById(R.id.label);
        }

        void bindTo(Card card) {
            mLabel.setText(card.getTitle());
        }
    }

    class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText, mInfoText;
        private ImageView mTopicImage, mTickView;
        private Animation in, out;
        private RelativeLayout mCardLayout;
        private com.google.android.material.floatingactionbutton.FloatingActionButton fab;
        private int flag;

        CardViewHolder(final View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mTopicImage = itemView.findViewById(R.id.topicImage);
            mCardLayout = itemView.findViewById(R.id.singlecardlayout);
            mTickView = itemView.findViewById(R.id.tick);

            @SuppressLint("InflateParams") View myLayout = LayoutInflater.from(mContext).inflate(R.layout.activity_topic_cards, null);
            fab = myLayout.findViewById(R.id.fab);

            fab.animate().alpha(0.0f);
            mTickView.animate().alpha(0.0f);
            flag = 0;

            in = AnimationUtils.loadAnimation(mContext, R.anim.cards_subtext_in);
            out = AnimationUtils.loadAnimation(mContext, R.anim.cards_subtext_out);

            in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCardLayout.setLayoutTransition(null);
                    mInfoText.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCardLayout.setLayoutTransition(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mInfoText.setTranslationY(-170f);
            mInfoText.setVisibility(GONE);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(v -> {
                mCardLayout.setLayoutTransition(new LayoutTransition());
                if (mInfoText.getVisibility() != View.VISIBLE) {
                    mInfoText.setVisibility(View.VISIBLE);
                    mInfoText.startAnimation(out);
                } else mInfoText.startAnimation(in);
                return true;
            });
        }

        void bindTo(Card currentTopic) {
            mTitleText.setText(currentTopic.getTitle());
            mInfoText.setText(currentTopic.getInfo());
            Glide.with(mContext)
                    .load(currentTopic.getImageResource())
                    .into(mTopicImage);
            mTopicImage.setColorFilter(Color.rgb(200, 200, 200), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public void onClick(View view) {
            if (flag == 0) {
                Animation expandIn = AnimationUtils.loadAnimation(mContext, R.anim.fab_pop);
                fab.startAnimation(expandIn);
                flag = 1;
            }
            if (mTickView.getAlpha() == 1.0f) {
                mTickView.animate().alpha(0.0f);
                CardsAdapter.this.updateCardNames(mTitleText.getText().toString(), 2);
            } else {
                mTickView.animate().alpha(1.0f);
                CardsAdapter.this.updateCardNames(mTitleText.getText().toString(), 1);
            }
        }
    }
}
