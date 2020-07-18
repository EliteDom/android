package com.nthusiast.app.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.communities.TopicCards;
import com.nthusiast.app.ui.login.LoginActivity;
import com.nthusiast.app.ui.profile.UserProfile;
import com.nthusiast.app.ui.search.Search;

import java.util.ArrayList;
import java.util.Objects;

public class Feed extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<PreviewCard> mTitleData;
    private ArrayList<String> mTopicNames;
    private FirebaseFirestore mDatabase;
    private FloatingActionButton fab;
    private PreviewAdapter mAdapter;
    private RecyclerView mRecycler;
    private boolean isRotated;
    private Uri localUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ConstraintLayout constraintLayout = findViewById(R.id.feed_container);
        mDatabase = FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_mainfeed);
//        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
//        animationDrawable.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);

        mRecycler = findViewById(R.id.recyclerView);
        mRecycler.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecycler.setClipToOutline(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 1);
        mRecycler.setAdapter(mAdapter);
        mTopicNames = getIntent().getStringArrayListExtra("cards");
        if (getIntent().getStringExtra("image") != null)
            localUri = Uri.parse(getIntent().getStringExtra("image"));

        isRotated = false;
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::rotateSequence);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::initializeData);
        initializeData();
    }

    private void rotateSequence(View v) {
        FloatingActionButton fabLogout = findViewById(R.id.fabLogout);
        FloatingActionButton fabProfile = findViewById(R.id.fabProfile);
        FloatingActionButton fabSearch = findViewById(R.id.fabSearch);
        rotateFab(v, !isRotated);
        if (isRotated) {
            fabLogout.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
            fabProfile.animate().translationY(-getResources().getDimension(R.dimen.standard_115));
            fabSearch.animate().translationY(-getResources().getDimension(R.dimen.standard_165));
        } else {
            fabLogout.animate().translationY(0);
            fabProfile.animate().translationY(0);
            fabSearch.animate().translationY(0);
        }
    }

    private void initializeData() {
        mTitleData.clear();
        if (localUri != null) {
            mTitleData.add(new PreviewCard(localUri));
            loadPosts();
        } else
            mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            mTitleData.add(new PreviewCard(Uri.parse("" + document.get("image"))));
                            loadPosts();
                        }
                    });
    }

    private void loadPosts() {
        for (final String i : mTopicNames) {
            mDatabase.collection("dorms").document(i).collection("posts")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.get("image") != null)
                                    mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), (String) document.get("author"), document.getId(), i, Uri.parse((String) document.get("image"))));
                                else
                                    mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), (String) document.get("author"), document.getId(), i));
                            }
                            mAdapter.notifyDataSetChanged();
                            runLayoutAnimation(mRecycler);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void profileUI(View view) {
        startActivity(new Intent(this, UserProfile.class));
        setResult(Activity.RESULT_OK);
        rotateSequence(fab);
    }

    public void topicUI(View view) {
        startActivity(new Intent(this, TopicCards.class));
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Feed.this, LoginActivity.class));
        setResult(RESULT_OK);
        finish();
    }

    public void searchActivity(View view) {
        startActivity(new Intent(this, Search.class));
        setResult(Activity.RESULT_OK);
        rotateSequence(fab);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_topic_cards_animation);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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
}
