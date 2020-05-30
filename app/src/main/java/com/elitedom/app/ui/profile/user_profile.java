package com.elitedom.app.ui.profile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.elitedom.app.ui.main.PreviewAdapter;
import com.elitedom.app.ui.main.PreviewCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class user_profile extends AppCompatActivity {

    private ArrayList<PreviewCard> mTitleData;
    private PreviewAdapter mAdapter;
    private FirebaseFirestore mDatabase;
    private ArrayList<String> mTopicNames;
    private TextView username, appreciation, predictor;
    private String currentDorm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        mDatabase = FirebaseFirestore.getInstance();
        username = findViewById(R.id.username);
        appreciation = findViewById(R.id.appreciation_score);
        predictor = findViewById(R.id.predictor_score);
        currentDorm = "";

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 2);
        mAdapter.sendContext(findViewById(R.id.profile_image), findViewById(R.id.username), findViewById(R.id.user_profile_holder));
        mRecyclerView.setAdapter(mAdapter);

        mTopicNames = getIntent().getStringArrayListExtra("cards");
        initializeData();
    }

    private void initializeData() {
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            predictor.setText(Objects.requireNonNull(document.get("predictorPoints")).toString());
                            appreciation.setText(Objects.requireNonNull(document.get("appreciationPoints")).toString());
                            username.setText(Objects.requireNonNull(document.get("firstName")).toString() + " " + Objects.requireNonNull(document.get("lastName")).toString() + "'s Profile");
                        }
                    }
                });
        mTitleData.clear();
        for (String i : mTopicNames) {
            currentDorm = i;
            mDatabase.collection("dorms").document(i).collection("posts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                                    mTitleData.add(new PreviewCard((String) document.get("title"), (String) document.get("postText"), document.get("author") + " | Authored " + document.get("timestamp") + " ago", document.getId(), user_profile.this.currentDorm, Uri.parse((String) document.get("image"))));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
