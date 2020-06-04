package com.elitedom.app.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.elitedom.app.ui.cards.topic_cards;
import com.elitedom.app.ui.login.LoginActivity;
import com.elitedom.app.ui.profile.user_profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Feed extends AppCompatActivity {

    private ArrayList<PreviewCard> mTitleData;
    private PreviewAdapter mAdapter;
    private FirebaseFirestore mDatabase;
    private ArrayList<String> mTopicNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RelativeLayout relativeLayout = findViewById(R.id.feed_container);
        mDatabase = FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.mainfeed_actionbar);
        //        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 1);
        mRecyclerView.setAdapter(mAdapter);
        mTopicNames = getIntent().getStringArrayListExtra("cards");

        initializeData();
    }

    private void initializeData() {
        mTitleData.clear();
        for (final String i : mTopicNames) {
            mDatabase.collection("dorms").document(i).collection("posts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                                    mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), document.get("author") + " | Authored " + document.get("timestamp") + " ago", document.getId(), i, Uri.parse((String) document.get("image"))));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void profileUI(View view) {
        Intent feed = new Intent(this, user_profile.class);
        feed.putExtra("cards", mTopicNames);
        startActivity(feed);
        setResult(Activity.RESULT_OK);
    }

    public void topicUI(View view) {
        startActivity(new Intent(this, topic_cards.class));
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Feed.this, LoginActivity.class));
        setResult(RESULT_OK);
        finish();
    }
}
