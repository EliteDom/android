package com.elitedom.app.ui.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class Result extends AppCompatActivity {

    private TextView mScore, mResultText;
    private FirebaseFirestore mDatabase;
    private String score, dorm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ConstraintLayout constraintLayout = findViewById(R.id.result_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        animationDrawable.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);

        mScore = findViewById(R.id.score);
        mResultText = findViewById(R.id.result_status);
        score = getIntent().getStringExtra("score");
        dorm = getIntent().getStringExtra("dorm");
        mDatabase = FirebaseFirestore.getInstance();

        displayResults();
    }

    @SuppressLint("SetTextI18n")
    private void displayResults() {
        mScore.setText(score + "/50");

        if (Integer.parseInt(Objects.requireNonNull(score)) < 40) mResultText.setText("Unsuccessful Attempt");
        else {
            mResultText.setText("You are now eligible to post at " + dorm + "!");
            mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<String> dorms = (ArrayList<String>) Objects.requireNonNull(task.getResult()).get("eliteDorms");
                            Objects.requireNonNull(dorms).add(dorm);
                            mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .update("eliteDorms", dorms);
                        }
                    });
        }
    }
}