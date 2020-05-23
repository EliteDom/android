package com.elitedom.app.ui.profile;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;

import java.util.Objects;

public class profile_post extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_post);

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

    public void sharedexpansion(View view) {
        Intent intent = new Intent(this, user_profile.class);
        Pair<View, String> t1 = Pair.create(findViewById(R.id.profile_image), "image");
        Pair<View, String> t2 = Pair.create(findViewById(R.id.username), "username");
        Pair<View, String> t3 = Pair.create(findViewById(R.id.user_profile_holder), "post_cards");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, t1, t2, t3);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}