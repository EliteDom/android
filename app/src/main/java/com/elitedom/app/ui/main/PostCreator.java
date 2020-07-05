package com.elitedom.app.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class PostCreator extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private ImageView postImage;
    private boolean isRotated;

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
        isRotated = false;

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

        fab.setOnClickListener(v -> {
            rotateFab(v, !isRotated);
            if (isRotated) {
                fabTick.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
                fabDecline.animate().translationY(-getResources().getDimension(R.dimen.standard_115));
            } else {
                fabTick.animate().translationY(0);
                fabDecline.animate().translationY(0);
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

    private void rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        isRotated = rotate;
    }

    public void submitPost(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard Post")
                .setMessage("Confirm Discard?")
                .setPositiveButton("Exit", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show())
                .setNeutralButton("Continue Editing", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Continue Editing", Toast.LENGTH_SHORT).show())
                .show();
    }

    public void discardPost(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard Post")
                .setMessage("Confirm Discard?")
                .setPositiveButton("Exit", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show())
                .setNeutralButton("Continue Editing", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "Continue Editing", Toast.LENGTH_SHORT).show())
                .show();
    }
}