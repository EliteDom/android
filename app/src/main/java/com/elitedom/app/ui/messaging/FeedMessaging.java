package com.elitedom.app.ui.messaging;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class FeedMessaging extends AppCompatActivity {

    private ArrayList<Message> messageArrayList;
    private MessageAdapter mAdapter;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_messaging);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        RecyclerView mRecyclerView = findViewById(R.id.messageList);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseFirestore.getInstance();
        messageArrayList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, messageArrayList);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initializeData() {
        // TODO: Configure Database Reads
        messageArrayList.clear();
        messageArrayList.add(new Message("Have you considered container implementation? ", "11:34", "Jon Skeet", Uri.parse("https://avatars1.githubusercontent.com/u/17011?s=460&v=4")));
        messageArrayList.add(new Message("I am familiar with Docker, but how does it affect modularity?", "11:38"));
        messageArrayList.add(new Message("Containerizing packages, not the OS [like Docker] would allow users to run only the packages they need for a workflow. As a result, they use the most minimal set of system resourrces without sacrificing on general-case usability.", "11:40", "Jon Skeet", Uri.parse("https://avatars1.githubusercontent.com/u/17011?s=460&v=4")));
        messageArrayList.add(new Message("That's interesting! I'll read up on this, and make a post audit.", "11:40"));
        messageArrayList.add(new Message("Happy to help!", "11:40", "Jon Skeet", Uri.parse("https://avatars1.githubusercontent.com/u/17011?s=460&v=4")));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {
    }
}
