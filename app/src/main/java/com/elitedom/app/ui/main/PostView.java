package com.elitedom.app.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.elitedom.app.ui.messaging.FeedMessaging;

import java.util.Objects;

public class PostView extends AppCompatActivity {

    private CardView mCard;
    private TextView mPostTitle, mPostText;
    private ImageView mLiked, mDisliked;
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    public void messageActivity(View view) {
        Intent intent = new Intent(this, FeedMessaging.class);
        intent.putExtra("uid", mPostTitle.getContentDescription().toString());
        intent.putExtra("dorm", mPostText.getContentDescription().toString());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), mCard, "messaging_transition");
        ActivityCompat.startActivity(this, intent, options.toBundle());
        setResult(Activity.RESULT_OK);
    }

    public void likePost(View view) {
        if (dislike_status == 1) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_liked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 0;
        }
        if (like_status == 0) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_unliked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 1;
        }
        else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_liked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 0;
        }
    }

    public void dislikePost(View view) {
        if (like_status == 1) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_up_liked_24dp);
            mLiked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            like_status = 0;
        }
        if (dislike_status == 0) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_unliked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 1;
        }
        else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.ic_thumb_down_liked_24dp);
            mDisliked.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            dislike_status = 0;
        }
    }
}
