package com.elitedom.app.ui.profile;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;

import java.util.Objects;

public class user_profile_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        TextView mPostTitle = findViewById(R.id.title);
        ImageView mPostImage = findViewById(R.id.postImage);
        TextView mPostText = findViewById(R.id.post_text);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent intent = getIntent();
        mPostTitle.setText(intent.getStringExtra("title"));
        mPostText.setText(intent.getStringExtra("subtext"));
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPostImage);

        mPostImage.setClipToOutline(true);
        mPostText.setClipToOutline(true);
    }

    public void backAction(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }
}
