package com.elitedom.app.ui.dorms;

import android.content.Context;
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
import com.elitedom.app.R;
import com.elitedom.app.ui.main.PreviewAdapter;
import com.elitedom.app.ui.main.PreviewCard;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class DormProfile extends AppCompatActivity {

    private TextView mNoPosts, mDormTitle, mDormAbout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<PreviewCard> mTitleData;
    private FirebaseFirestore mDatabase;
    private PreviewAdapter mAdapter;
    private RecyclerView mRecycler;
    private String dorm, imageUri;
    private ImageView dormBanner;
    private ImageButton mFollow;
    private int follow_status;

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
        imageUri = getIntent().getStringExtra("im=-age");
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
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 1);
        mRecycler.setAdapter(mAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::initializeData);
        initializeData();
    }

    private void initializeData() {
        follow_status = 0;
        Glide.with(this)
                .load(imageUri)
                .into(dormBanner);
        mDormTitle.setText(dorm);
        mDatabase.collection("dorms").document(dorm)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) mDormAbout.setText((String) Objects.requireNonNull(task.getResult()).get("about"));
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
                                mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), document.get("author") + " | Authored " + document.get("timestamp") + " ago", document.getId(), dorm, Uri.parse((String) document.get("image"))));
                            else
                                mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), document.get("author") + " | Authored " + document.get("timestamp") + " ago", document.getId(), dorm));
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
        if (follow_status == 1) {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.bookmark_out_24dp);
            mFollow.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            follow_status = 0;
        } else {
            AnimatedVectorDrawable animatedVectorDrawable =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.bookmark_in_24dp);
            mFollow.setImageDrawable(animatedVectorDrawable);
            assert animatedVectorDrawable != null;
            animatedVectorDrawable.start();
            follow_status = 1;
        }
    }
}