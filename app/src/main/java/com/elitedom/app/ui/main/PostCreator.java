package com.elitedom.app.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class PostCreator extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private ImageView postImage;
    private boolean isRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);
        ConstraintLayout constraintLayout = findViewById(R.id.feed_container);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        animationDrawable.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);

        EditText title = findViewById(R.id.editor_title);
        ImageView profileImage = findViewById(R.id.image_profile);
        postImage = findViewById(R.id.postImage);
        isRotate = false;

        Glide.with(this)
                .load(getIntent().getStringExtra("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);
        profileImage.setContentDescription(getIntent().getStringExtra("image"));
        title.setText(getIntent().getStringExtra("title"));
        profileImage.setClipToOutline(true);
        postImage.setClipToOutline(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton fabTick = findViewById(R.id.fabTick);
        FloatingActionButton fabDecline = findViewById(R.id.fabDecline);

        ViewAnimator.init(fabTick);
        ViewAnimator.init(fabDecline);
        fab.setOnClickListener(v -> {
            isRotate = ViewAnimator.rotateFab(v, !isRotate);
            if(isRotate){
                ViewAnimator.showIn(fabTick);
                ViewAnimator.showIn(fabDecline);
            }else{
                ViewAnimator.showOut(fabTick);
                ViewAnimator.showOut(fabDecline);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Glide.with(this)
                    .load(data.getData())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(postImage);
        }
    }

    @SuppressLint("IntentReset")
    public void imageInput(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }
}