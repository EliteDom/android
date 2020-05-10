package com.elitedom.app.ui.cards;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class topic_cards extends AppCompatActivity {

    private DatabaseReference mDatabase;
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

        mTopicData = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dorms");
        RelativeLayout relativeLayout = findViewById(R.id.card_container);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CardsAdapter(this, mTopicData);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        final ItemTouchHelper helper = new ItemTouchHelper(new
                                                                   ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                                           ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                                                       @Override
                                                                       public boolean onMove(@NonNull RecyclerView recyclerView,
                                                                                             @NonNull RecyclerView.ViewHolder viewHolder,
                                                                                             @NonNull RecyclerView.ViewHolder target) { return false; }
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
        TypedArray topicTitleResources = getResources().obtainTypedArray(R.array.topic_images);
        mTopicData.clear();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { loadImages((Map<String,Object>) dataSnapshot.getValue()); }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        topicTitleResources.recycle();
        mAdapter.notifyDataSetChanged();
    }

    public void feedActivity(View view) {
        Intent feed = new Intent(this, Feed.class);
        startActivity(feed);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void loadImages(Map<String, Object> images) {
        for (Map.Entry<String, Object> entry : images.entrySet()) {
            Map singleImage = (Map) entry.getValue();
            Uri image = Uri.parse((String) singleImage.get("image"));
            String topic = (String) singleImage.get("Name");
            String subtopic = (String) singleImage.get("description");
            Log.d("IMAGE URI", "Image URI is: " + image);
            Log.d("IMAGE URI", "Name URI is: " + topic);
            Log.d("IMAGE URI", "Description URI is: " + subtopic);
            mTopicData.add(new Cards(topic, subtopic, image));
        }
    }
}