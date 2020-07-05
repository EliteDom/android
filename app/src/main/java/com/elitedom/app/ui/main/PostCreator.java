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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class PostCreator extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private FirebaseFirestore mDatabase;
    private ArrayList submitDorms;
    private ImageView postImage;
    private boolean isRotated;
    private EditText title, body;

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

        ImageView profileImage = findViewById(R.id.image_profile);
        title = findViewById(R.id.editor_title);
        body = findViewById(R.id.editor_body);
        postImage = findViewById(R.id.postImage);
        mDatabase = FirebaseFirestore.getInstance();
        submitDorms = new ArrayList<>();
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
        getDorms();
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

    public void getDorms() {
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) submitDorms = (ArrayList) document.get("eliteDorms");
                        else submitDorms = new ArrayList<>();
                    }
                });
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
        if (body.getText().toString().length() > 0 && title.getText().toString().length() > 0) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Pick a Dorm!")
                    .setItems(getStringArray(submitDorms), (dialog, which) -> {

                    })
                    .show();
        }
    }

    public void discardPost(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Discard Post")
                .setMessage("Confirm Discard?")
                .setPositiveButton("Exit", (dialogInterface, i) -> supportFinishAfterTransition())
                .setNeutralButton("Continue Editing", (dialogInterface, i) -> {
                })
                .show();
    }

    private String[] getStringArray(ArrayList<String> arr)
    {
        String[] str_arr = new String[arr.size()];
        for (int j = 0; j < arr.size(); j++) {
            str_arr[j] = arr.get(j);
        }
        return str_arr;
    }
}