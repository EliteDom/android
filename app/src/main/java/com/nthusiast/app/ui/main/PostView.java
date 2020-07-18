package com.nthusiast.app.ui.main;

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
import com.nthusiast.app.R;
import com.nthusiast.app.ui.messaging.FeedMessaging;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostView extends AppCompatActivity {

    private CardView mCard;
    private long appreciations;
    private ImageView mLiked, mDisliked;
    private FirebaseFirestore mDatabase;
    private TextView mPostTitle, mPostText;
    private int like_status, dislike_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        RelativeLayout relativeLayout = findViewById(R.id.single_card);
        mPostText = findViewById(R.id.post_text);
        TextView mAuthor = findViewById(R.id.author);
        ImageView mPostImage = findViewById(R.id.postImage);
        mCard = findViewById(R.id.action_cards);
        mPostTitle = findViewById(R.id.title);
        mLiked = findViewById(R.id.liked);
        mDisliked = findViewById(R.id.disliked);
        mDatabase = FirebaseFirestore.getInstance();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
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
        mAuthor.setText(intent.getStringExtra("author"));
        if (Objects.requireNonNull(intent.getStringExtra("image")).length() > 0)
            mPostImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPostImage);
        mPostImage.setClipToOutline(true);
        mPostText.setClipToOutline(true);

        mDatabase.collection("dorms").document(mPostText.getContentDescription().toString()).collection("posts").document(mPostTitle.getContentDescription().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.get("apprs") != null)
                            appreciations = (long) document.get("apprs");
                    }
                });
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("postActions").document(mPostTitle.getContentDescription().toString())
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
        Intent intent = new Intent(this, FeedMessaging.class);
        intent.putExtra("uid", mPostTitle.getContentDescription().toString());
        intent.putExtra("dorm", mPostText.getContentDescription().toString());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), mCard, "messaging_transition");
        ActivityCompat.startActivity(this, intent, options.toBundle());
        setResult(Activity.RESULT_OK);
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
