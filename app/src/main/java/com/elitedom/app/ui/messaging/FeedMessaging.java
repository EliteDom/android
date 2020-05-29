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
        messageArrayList.add(new Message("Hello there", "11:30"));
        messageArrayList.add(new Message("General Kenobi", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Ah, I see you're a man of culture as well", "11:31"));
        messageArrayList.add(new Message("Indeed", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Hello there", "11:30"));
        messageArrayList.add(new Message("General Kenobi", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Ah, I see you're a man of culture as well", "11:31"));
        messageArrayList.add(new Message("Indeed", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Indeed", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Hello there", "11:30"));
        messageArrayList.add(new Message("Hello there", "11:30"));
        messageArrayList.add(new Message("General Kenobi", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Ah, I see you're a man of culture as well", "11:31"));
        messageArrayList.add(new Message("Indeed", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Hello there", "11:30"));
        messageArrayList.add(new Message("General Kenobi", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        messageArrayList.add(new Message("Ah, I see you're a man of culture as well", "11:31"));
        messageArrayList.add(new Message("Indeed", "11:31", "Jinen Setpal", Uri.parse("https://avatars1.githubusercontent.com/u/52078103?s=400&u=77322297039f4d6c78f3c968aead567dbe73abfe&v=4")));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {
    }
}
