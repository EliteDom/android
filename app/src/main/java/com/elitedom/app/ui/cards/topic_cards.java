package com.elitedom.app.ui.cards;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.elitedom.app.ui.main.Feed;

import java.util.ArrayList;
import java.util.Objects;

public class topic_cards extends AppCompatActivity {

    private ArrayList<Cards> mTopicData;
    private CardsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cards);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        // Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.card_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTopicData = new ArrayList<>();

        mAdapter = new CardsAdapter(this, mTopicData);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        final ItemTouchHelper helper = new ItemTouchHelper(new
                                                                   ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                                           ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                                                       @Override
                                                                       public boolean onMove(@NonNull RecyclerView recyclerView,
                                                                                             @NonNull RecyclerView.ViewHolder viewHolder,
                                                                                             @NonNull RecyclerView.ViewHolder target) {
/*
                                                                           int from = viewHolder.getAdapterPosition();
                                                                           int to = target.getAdapterPosition();
                                                                           Collections.swap(mTopicData, from, to);
                                                                           mAdapter.notifyItemMoved(from, to);
                                                                           Disallow Card Reorganisation*/
                                                                           return false;
                                                                       }

                                                                       @Override
                                                                       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                                                                            int direction) {
                                                                           mTopicData.remove(viewHolder.getAdapterPosition());
                                                                           mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                                                       }
                                                                   });
        helper.attachToRecyclerView(mRecyclerView);
    }

    private void initializeData() {
        String[] topicList = getResources()
                .getStringArray(R.array.topic_titles);
        String[] topicInfo = getResources()
                .getStringArray(R.array.topic_info);
        TypedArray topicTitleResources = getResources().obtainTypedArray(R.array.topic_images);
        mTopicData.clear();

        for (int i = 0; i < topicList.length; i++) {
            mTopicData.add(new Cards(topicList[i], topicInfo[i],
                    topicTitleResources.getResourceId(i, 0)));
        }
        topicTitleResources.recycle();
        mAdapter.notifyDataSetChanged();
    }

    public void feedActivity(View view) {
        Intent feed = new Intent(this, Feed.class);
        startActivity(feed);
        setResult(Activity.RESULT_OK);
        finish();
    }
}