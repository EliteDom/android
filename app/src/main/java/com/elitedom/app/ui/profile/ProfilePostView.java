package com.elitedom.app.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.elitedom.app.ui.messaging.ProfileMessaging;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePostView extends AppCompatActivity {

    private CardView mCard;
    private long appreciations;
    private String profileImageUri;
    private ImageView mLiked, mDisliked;
    private FirebaseFirestore mDatabase;
    private int like_status, dislike_status;
    private TextView mUsername, mPostTitle, mPostText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_post);

        CircleImageView mProfileImage = findViewById(R.id.profile_image);
        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        ImageView mPostImage = findViewById(R.id.postImage);
        TextView mAuthor = findViewById(R.id.author);
        mPostText = findViewById(R.id.post_text);
        mCard = findViewById(R.id.action_cards);
        mUsername = findViewById(R.id.username);
        mPostTitle = findViewById(R.id.title);
        mLiked = findViewById(R.id.liked);
        mDisliked = findViewById(R.id.disliked);
        mDatabase = FirebaseFirestore.getInstance();

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent intent = getIntent();
        mPostTitle.setText(intent.getStringExtra("title"));
        mPostTitle.setContentDescription(intent.getStringExtra("uid"));
        mPostText.setText(intent.getStringExtra("subtext"));
        mPostText.setContentDescription(intent.getStringExtra("dorm"));
        profileImageUri = intent.getStringExtra("profileImage");
        Glide.with(this)
                .load(profileImageUri)
                .into(mProfileImage);
        mAuthor.setText(intent.getStringExtra("author"));
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPostImage);

        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        mUsername.setText(Objects.requireNonNull(document.get("firstName")).toString() + " " + Objects.requireNonNull(document.get("lastName")).toString() + "'s Profile");
                    }
                });

        mPostImage.setClipToOutline(true);
        mPostText.setClipToOutline(true);

        mDatabase.collection("dorms").document(mPostText.getContentDescription().toString()).collection("posts").document(mPostTitle.getContentDescription().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists())
                            appreciations = (long) document.get("apprs");
                    }
                });

        mDatabase.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("postActions").document(mPostTitle.getContentDescription().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            long status = (long) document.get("status");
                            if (status == 0) {
                                dislike_status = 1;
                                mDisliked.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                            } else if (status == 1) {
                                like_status = 1;
                                mLiked.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                            }
                        }
                    }
                });
    }

    public void backAction(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    public void messageActivity(View view) {
        Intent intent = new Intent(this, ProfileMessaging.class);
        intent.putExtra("username", mUsername.getText().toString());
        intent.putExtra("uid", mPostTitle.getContentDescription().toString());
        intent.putExtra("dorm", mPostText.getContentDescription().toString());
        intent.putExtra("profileImage", profileImageUri);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), mCard, "messaging_transition");
        ActivityCompat.startActivity(this, intent, options.toBundle());
        setResult(Activity.RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void likeIcon(View view) {
        if (dislike_status == 1) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_liked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 0;
            incrementVote(1);
        }
        if (like_status == 0) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_unliked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 1;
            incrementVote(1);
        } else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_liked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 0;
            decrementVote(2);
        }
    }

    public void dislikeIcon(View view) {
        if (like_status == 1) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_liked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 0;
            decrementVote(0);
        }
        if (dislike_status == 0) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_unliked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 1;
            decrementVote(0);
        } else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_liked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 0;
            incrementVote(2);
        }
    }

    private void incrementVote(int statusId) {
        Map<String, Object> data = new HashMap<>();
        data.put("apprs", appreciations + 1);
        mDatabase.collection("dorms").document(mPostText.getContentDescription().toString()).collection("posts").document(mPostTitle.getContentDescription().toString())
                .set(data, SetOptions.merge());
        appreciations += 1;
        data = new HashMap<>();
        data.put("status", statusId);
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("postActions").document(mPostTitle.getContentDescription().toString())
                .set(data, SetOptions.merge());
    }

    private void decrementVote(int statusId) {
        Map<String, Object> data = new HashMap<>();
        data.put("apprs", appreciations - 1);
        mDatabase.collection("dorms").document(mPostText.getContentDescription().toString()).collection("posts").document(mPostTitle.getContentDescription().toString())
                .set(data, SetOptions.merge());
        data = new HashMap<>();
        data.put("status", statusId);
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("postActions").document(mPostTitle.getContentDescription().toString())
                .set(data, SetOptions.merge());
        appreciations -= 1;
    }

    public void sharePost(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mPostText.getText().toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}