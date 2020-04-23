package com.elitedom.app.ui.main;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;

import java.util.ArrayList;
import java.util.Objects;

public class Feed extends AppCompatActivity {

    private ArrayList<PreviewCard> mTitleData;
    private PreviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RelativeLayout relativeLayout = findViewById(R.id.feed_container);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();
    }

    private void initializeData() {
        String[] topicList = getResources()
                .getStringArray(R.array.dummy_posts);
        String[] topicInfo = getResources()
                .getStringArray(R.array.topic_info);
        TypedArray topicTitleResources = getResources().obtainTypedArray(R.array.topic_images);
        mTitleData.clear();

        for (int i = 0; i < topicList.length; i++) {
            mTitleData.add(new PreviewCard(topicList[i], topicInfo[i],
                    topicTitleResources.getResourceId(i, 0)));
        }
        topicTitleResources.recycle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
