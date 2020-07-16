package com.elitedom.app.ui.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elitedom.app.R;
import com.elitedom.app.ui.dorms.DormProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Quiz extends AppCompatActivity {

    private TextView time, question, option_1, option_2, option_3, option_4;
    private FirebaseFirestore mDatabase;
    private HashMap<String, ArrayList<String>> quiz;
    private String dorm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ConstraintLayout constraintLayout = findViewById(R.id.quiz_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        animationDrawable.setColorFilter(Color.rgb(190, 190, 190), android.graphics.PorterDuff.Mode.MULTIPLY);

        mDatabase = FirebaseFirestore.getInstance();
        time = findViewById(R.id.time);
        question = findViewById(R.id.question);
        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        option_3 = findViewById(R.id.option_3);
        option_4 = findViewById(R.id.option_4);
        dorm = getIntent().getStringExtra("dorm");
        quiz = new HashMap<>();

        initializeData();
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Start Quiz?")
                .setMessage("Succeed to get post access to " + dorm + "!")
                .setPositiveButton("Attempt", (dialogInterface, i) -> quizHandler())
                .setNeutralButton("Return", (dialogInterface, i) -> finish())
                .show();
    }

    private void quizHandler() {
        final int[] b = {0};
        quiz.forEach((key,value) -> {
            if (b[0] == 0) b[0] = 1;
            animateText(question, key);
            animateText(option_1, value.get(0));
            animateText(option_2, value.get(1));
            animateText(option_3, value.get(2));
            animateText(option_4, value.get(3));
        });
    }

    private void initializeData() {
        mDatabase.collection("dorms").document(dorm).collection("quiz")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            quiz.put((String) document.get("question"), (ArrayList<String>) document.get("options"));
                });
    }

    private void removeTextAnimation(final TextView view) {
        new Thread(() -> {
            while (view.getText().toString().length() > 1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                view.post(() -> view.setText(view.getText().toString().substring(0, view.getText().toString().length() - 1)));
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void countDown(final TextView view) {
        ProgressBar timer = findViewById(R.id.timer);
        timer.setMax(100000);

        ObjectAnimator animation = ObjectAnimator.ofInt(timer, "progress", 100 * 1000, 0);
        animation.setDuration(10000);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        view.setText("10");
        new Thread(() -> {
            for (int i = 9; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String finalI = String.valueOf(i);
                view.post(() -> view.setText(finalI));
            }
        }).start();
    }

    private void animateText(final TextView view, String text) {
        final char[] s = text.toCharArray();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            for (final char ch : s) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                view.post(() -> view.append("" + ch));
            }
        }).start();
    }
}