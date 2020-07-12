package com.elitedom.app.ui.search;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Search extends AppCompatActivity {

    private Map<String, Uri> dorms;
    private ArrayList<Result> resultArrayList;
    private FirebaseFirestore mDatabase;
    private ResultAdapter mAdapter;
    private CardView mResultCard;
    private TextView mNoResults;
    private EditText mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ConstraintLayout constraintLayout = findViewById(R.id.feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        resultArrayList = new ArrayList<>();
        dorms = new HashMap<>();
        mAdapter = new ResultAdapter(this, resultArrayList);
        mDatabase = FirebaseFirestore.getInstance();
        mQuery = findViewById(R.id.search_text);
        mResultCard = findViewById(R.id.result_card);
        mNoResults = findViewById(R.id.no_results);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        initializeData();
    }

    private void initializeData() {
        mDatabase.collection("dorms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            dorms.put(document.getId(), Uri.parse((String) document.get("image")));
                    }
                });
    }

    public void triggerSearch(View view) {
        String query = mQuery.getText().toString();
        resultArrayList.clear();
        for (Map.Entry<String, Uri> dorm : dorms.entrySet())
            if (dorm.getKey().toLowerCase().contains(query.toLowerCase()))
                resultArrayList.add(new Result(dorm.getKey(), dorm.getValue()));
        mAdapter.notifyDataSetChanged();
        if (resultArrayList.isEmpty()) {
            mNoResults.animate().alpha(1.0f);
            mResultCard.animate().alpha(0.0f);
        }
        else {
            mNoResults.animate().alpha(0.0f);
            mResultCard.animate().alpha(1.0f);
        }
    }
}