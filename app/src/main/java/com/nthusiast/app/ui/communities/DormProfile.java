package com.nthusiast.app.ui.communities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.main.PostCreator;
import com.nthusiast.app.ui.main.PreviewAdapter;
import com.nthusiast.app.ui.main.PreviewCard;
import com.nthusiast.app.ui.quiz.Quiz;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class DormProfile extends AppCompatActivity {

    private TextView mNoPosts, mDormTitle, mDormAbout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<PreviewCard> mTitleData;
    private FirebaseFirestore mDatabase;
    private boolean following, elite;
    private PreviewAdapter mAdapter;
    private ArrayList followedDorms;
    private RecyclerView mRecycler;
    private String dorm, imageUri;
    private ImageView dormBanner;
    private ImageButton mFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ConstraintLayout constraintLayout = findViewById(R.id.dorm_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        dorm = getIntent().getStringExtra("dorm");
        imageUri = getIntent().getStringExtra("image");
        mDormTitle = findViewById(R.id.title);
        mDormAbout = findViewById(R.id.about_text);
        mDatabase = FirebaseFirestore.getInstance();
        mNoPosts = findViewById(R.id.no_posts);
        mFollow = findViewById(R.id.follow_button);
        dormBanner = findViewById(R.id.dormBanner);
        dormBanner.setClipToOutline(true);

        mRecycler = findViewById(R.id.recyclerView);
        mRecycler.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecycler.setClipToOutline(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        followedDorms = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 1);
        mRecycler.setAdapter(mAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::initializeData);
        initializeData();
    }

    private void initializeData() {
        following = false;
        elite = false;

        Glide.with(this)
                .load(imageUri)
                .into(dormBanner);
        mDormTitle.setText(dorm);
        mDatabase.collection("dorms").document(dorm)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        mDormAbout.setText((String) Objects.requireNonNull(task.getResult()).get("description"));
                });

        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<String> submitDorms = new ArrayList<>();
                        if (document != null && document.get("eliteDorms") != null)
                            submitDorms = (ArrayList) document.get("eliteDorms");
                        if (Objects.requireNonNull(submitDorms).contains(dorm)) elite = true;
                    }
                });

        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.get("followedDorms") != null)
                            followedDorms = (ArrayList) document.get("followedDorms");
                        else followedDorms = new ArrayList<>();
                        assert followedDorms != null;
                        if (followedDorms.contains(dorm)) {
                            following = true;
                            mFollow.setImageResource(R.drawable.ic_bookmark_black_24dp);
                        } else {
                            following = false;
                            mFollow.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                        }
                    }
                });
        loadPosts();
    }

    private void loadPosts() {
        mTitleData.clear();
        mDatabase.collection("dorms").document(dorm).collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            if (document.get("image") != null)
                                mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), (String) document.get("author"), document.getId(), dorm, Uri.parse((String) document.get("image"))));
                            else
                                mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), (String) document.get("author"), document.getId(), dorm));
                        }
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1)
                            runLayoutAnimation(mRecycler);
                        else mNoPosts.animate().alpha(1.0f);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_topic_cards_animation);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void followButton(View view) {
        if (following) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.bookmark_out_24dp);
            mFollow.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            followedDorms.remove(dorm);
            following = false;
            Snackbar.make(mRecycler, "Unfollowed " + dorm + "!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show();
        } else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.bookmark_in_24dp);
            mFollow.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            followedDorms.add(dorm);
            following = true;
            Snackbar.make(mRecycler, "Followed " + dorm + "!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .update("followedDorms", followedDorms);
    }

    public void quizUI(View view) {
        if (!elite) {
            Intent quiz = new Intent(this, Quiz.class);
            quiz.putExtra("dorm", dorm);
            startActivity(quiz);
        } else {
            mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Intent post = new Intent(DormProfile.this, PostCreator.class);
                                if (document.get("image") != null)
                                    post.putExtra("image", (String) document.get("image"));
                                else post.putExtra("image", "");
                                startActivity(post);
                            }
                        }
                    });
        }
    }
}