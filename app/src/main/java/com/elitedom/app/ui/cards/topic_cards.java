package com.elitedom.app.ui.cards;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.elitedom.app.ui.main.Feed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class topic_cards extends AppCompatActivity {

    private FirebaseFirestore mDatabase;
    private ArrayList<Cards> mTopicData;
    private CardsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cards);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.tc_actionbar);
        // Objects.requireNonNull(getSupportActionBar()).hide();

        mTopicData = new ArrayList<>();

        mDatabase = FirebaseFirestore.getInstance();
        RelativeLayout relativeLayout = findViewById(R.id.card_container);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CardsAdapter(this, mTopicData);
        mRecyclerView.setAdapter(mAdapter);

        initializeData();

        final ItemTouchHelper helper = new ItemTouchHelper(new
                                                                   ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                                           ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                                                       @Override
                                                                       public boolean onMove(@NonNull RecyclerView recyclerView,
                                                                                             @NonNull RecyclerView.ViewHolder viewHolder,
                                                                                             @NonNull RecyclerView.ViewHolder target) { return false; }
                                                                       @Override
                                                                       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                                                                            int direction) {
                                                                           mTopicData.remove(viewHolder.getAdapterPosition());
                                                                           mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                                                       }
                                                                   });
        helper.attachToRecyclerView(mRecyclerView);
    }

    private void initializeData() {
        mTopicData.clear();
        mDatabase.collection("dorms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) mTopicData.add(new Cards((String) document.get("name"), (String) document.get("description"), Uri.parse((String) document.get("image"))));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    public void feedActivity(View view) {
        Intent feed = new Intent(this, Feed.class);
        startActivity(feed);
        setResult(Activity.RESULT_OK);
        finish();
    }
}