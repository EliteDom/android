package com.elitedom.app.ui.main;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elitedom.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Feed extends AppCompatActivity {

    private ArrayList<PreviewCard> mTitleData;
    private PreviewAdapter mAdapter;
    private DatabaseReference mDatabase;
    private ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RelativeLayout relativeLayout = findViewById(R.id.feed_container);
        imageList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dorms");

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

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) { loadImages((Map<String,Object>) dataSnapshot.getValue()); }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        for (int i = 0; i < topicList.length; i++) {
//            mTitleData.add(new PreviewCard())
            mTitleData.add(new PreviewCard(topicList[i], topicInfo[i],
                    topicTitleResources.getResourceId(i, 0)));
        }
        topicTitleResources.recycle();
        mAdapter.notifyDataSetChanged();
    }

    private void loadImages(Map<String, Object> images) {
        for (Map.Entry<String, Object> entry : images.entrySet()) {
            Map singleImage = (Map) entry.getValue();
            imageList.add((String) singleImage.get("images"));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
