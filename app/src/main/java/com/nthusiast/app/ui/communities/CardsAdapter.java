package com.nthusiast.app.ui.communities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.nthusiast.app.R;

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

        private MaterialCardView mCard;
        private TextView mTitleText, mInfoText;
        private ImageView mTopicImage;

        CardViewHolder(final View itemView) {
            super(itemView);

            mCard = itemView.findViewById(R.id.card_view);
            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mTopicImage = itemView.findViewById(R.id.topicImage);

            @SuppressLint("InflateParams") View myLayout = LayoutInflater.from(mContext).inflate(R.layout.activity_topic_cards, null);
            com.google.android.material.floatingactionbutton.FloatingActionButton fab = myLayout.findViewById(R.id.fab);

            fab.animate().alpha(0.0f);

            mInfoText.setTranslationY(-170f);
            mInfoText.setVisibility(GONE);
            mCard.setOnClickListener(this);
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
            if (mCard.isChecked()) {
                mCard.setChecked(false);
                CardsAdapter.this.updateCardNames(mTitleText.getText().toString(), 2);
            } else {
                mCard.setChecked(true);
                CardsAdapter.this.updateCardNames(mTitleText.getText().toString(), 1);
            }
        }
    }
}
