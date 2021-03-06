package com.nthusiast.app.ui.communities;

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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.main.Feed;

import java.util.ArrayList;
import java.util.Objects;

public class TopicCards extends AppCompatActivity {

    private ArrayList<String> followedDorms;
    private FirebaseFirestore mDatabase;
    private ArrayList<Card> mTopicData;
    private RecyclerView mRecycler;
    private CardsAdapter mAdapter;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cards);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_topic_cards);
        // Objects.requireNonNull(getSupportActionBar()).hide();

        mTopicData = new ArrayList<>();
        mDatabase = FirebaseFirestore.getInstance();
        RelativeLayout relativeLayout = findViewById(R.id.card_container);
        mRecycler = findViewById(R.id.recyclerView);
        if (getIntent().getStringExtra("image") != null)
            imageUri = getIntent().getStringExtra("image");

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mRecycler.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecycler.setClipToOutline(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CardsAdapter(this, mTopicData);
        mRecycler.setAdapter(mAdapter);

        initializeData();

        final ItemTouchHelper helper = new ItemTouchHelper(new
                                                                   ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                                           ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                                                       @Override
                                                                       public boolean onMove(@NonNull RecyclerView recyclerView,
                                                                                             @NonNull RecyclerView.ViewHolder viewHolder,
                                                                                             @NonNull RecyclerView.ViewHolder target) {
                                                                           return false;
                                                                       }

                                                                       @SuppressWarnings("deprecation")
                                                                       @Override
                                                                       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                                                                            int direction) {
                                                                           mTopicData.remove(viewHolder.getAdapterPosition());
                                                                           mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                                                       }
                                                                   });
        helper.attachToRecyclerView(mRecycler);
    }

    private void initializeData() {
        mTopicData.clear();
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        followedDorms = new ArrayList<>();
                        if (document != null && document.get("followedDorms") != null) {
                            mTopicData.add(new Card("Followed Communities"));
                            followedDorms = (ArrayList<String>) document.get("followedDorms");
                        }
                        for (String i : Objects.requireNonNull(followedDorms)) {
                            mDatabase.collection("dorms").document(i)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document1 = Objects.requireNonNull(task1.getResult());
                                            mTopicData.add(new Card((String) document1.get("name"), (String) document1.get("description"), Uri.parse((String) document1.get("image"))));
                                        }
                                        if (followedDorms.indexOf(i) == followedDorms.size() - 1)
                                            mDatabase.collection("dorms")
                                                    .get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task.isSuccessful()) {
                                                            mTopicData.add(new Card("Recommended Communities"));
                                                            for (QueryDocumentSnapshot document2 : Objects.requireNonNull(task2.getResult()))
                                                                //noinspection SuspiciousMethodCalls
                                                                if (!followedDorms.contains(document2.get("name")))
                                                                    mTopicData.add(new Card((String) document2.get("name"), (String) document2.get("description"), Uri.parse((String) document2.get("image"))));
                                                            mAdapter.notifyDataSetChanged();
                                                            runLayoutAnimation(mRecycler);
                                                        }
                                                    });
                                    });
                        }
                        if (followedDorms.size() == 0)
                            mDatabase.collection("dorms")
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task.isSuccessful()) {
                                            mTopicData.add(new Card("Recommended Dorms"));
                                            for (QueryDocumentSnapshot document2 : Objects.requireNonNull(task2.getResult()))
                                                mTopicData.add(new Card((String) document2.get("name"), (String) document2.get("description"), Uri.parse((String) document2.get("image"))));
                                            mAdapter.notifyDataSetChanged();
                                            runLayoutAnimation(mRecycler);
                                        }
                                    });
                    }
                });
    }

    public void feedActivity(View view) {
        if (mAdapter.getCardNames().size() > 0) {
            Intent feed = new Intent(this, Feed.class);
            if (imageUri != null) feed.putExtra("image", imageUri);
            feed.putExtra("cards", mAdapter.getCardNames());
            startActivity(feed);
            setResult(Activity.RESULT_OK);
            finish();
        } else
            Snackbar.make(findViewById(R.id.fab), "Please select a category!", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(findViewById(R.id.fab))
                    .show();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_topic_cards_animation);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}