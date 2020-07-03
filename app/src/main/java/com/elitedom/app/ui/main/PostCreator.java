package com.elitedom.app.ui.main;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;

import java.util.Objects;

public class PostCreator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);
        RelativeLayout relativeLayout = findViewById(R.id.feed_container);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_mainfeed);

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        animationDrawable.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);

        EditText title = findViewById(R.id.editor_title);
        ImageView profileImage = findViewById(R.id.image_profile);
        ImageView postImage = findViewById(R.id.postImage);

        Glide.with(this)
                .load(getIntent().getStringExtra("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);
        profileImage.setContentDescription(getIntent().getStringExtra("image"));
        title.setText(getIntent().getStringExtra("title"));
        profileImage.setClipToOutline(true);
        postImage.setClipToOutline(true);
    }

    public void profileUI(View view) {} // Nothing run, functions exist for exception handling only
    public void topicUI(View view) {}
    public void logOut(View view) {}

}