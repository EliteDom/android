package com.elitedom.app.ui.cards;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class topic_cards extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<Cards> mTopicData;
    private CardsAdapter mAdapter;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cards);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        relativeLayout = findViewById(R.id.card_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView);

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
                                                                       public boolean onMove(RecyclerView recyclerView,
                                                                                             RecyclerView.ViewHolder viewHolder,
                                                                                             RecyclerView.ViewHolder target) {
                                                                           int from = viewHolder.getAdapterPosition();
                                                                           int to = target.getAdapterPosition();
                                                                           Collections.swap(mTopicData, from, to);
                                                                           mAdapter.notifyItemMoved(from, to);
                                                                           return true;
                                                                       }

                                                                       @Override
                                                                       public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                                                                            int direction) {
                                                                           mTopicData.remove(viewHolder.getAdapterPosition());
                                                                           mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                                                       }
                                                                   });
        helper.attachToRecyclerView(mRecyclerView);
    }

    private void initializeData() {
        String[] sportsList = getResources()
                .getStringArray(R.array.sports_titles);
        String[] sportsInfo = getResources()
                .getStringArray(R.array.sports_info);
        TypedArray topicTitleResources = getResources().obtainTypedArray(R.array.topic_images);
        mTopicData.clear();

        for (int i = 0; i < sportsList.length; i++) {
            mTopicData.add(new Cards(sportsList[i], sportsInfo[i],
                    topicTitleResources.getResourceId(i, 0)));
        }
        topicTitleResources.recycle();
        mAdapter.notifyDataSetChanged();
    }
}