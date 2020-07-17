package com.elitedom.app.ui.quiz;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elitedom.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Quiz extends AppCompatActivity {

    private TextView time, question, option_1, option_2, option_3, option_4;
    private Iterator<HashMap.Entry<String, ArrayList<String>>> iterator;
    private HashMap<String, ArrayList<String>> quiz;
    private FirebaseFirestore mDatabase;
    private ProgressBar timer;
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

        dorm = getIntent().getStringExtra("dorm");
        mDatabase = FirebaseFirestore.getInstance();
        question = findViewById(R.id.question);
        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        option_3 = findViewById(R.id.option_3);
        option_4 = findViewById(R.id.option_4);
        timer = findViewById(R.id.timer);
        time = findViewById(R.id.time);
        quiz = new HashMap<>();

        initializeData();
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Start Quiz?")
                .setMessage("Succeed to get post access to " + dorm + "!")
                .setPositiveButton("Attempt", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    iterator = quiz.entrySet().iterator();
                    runQuestion();
                })
                .setNeutralButton("Return", (dialogInterface, i) -> finish())
                .show();
    }

    private void runQuestion() {
        if (iterator.hasNext()) {
            HashMap.Entry<String, ArrayList<String>> entry = iterator.next();
            animateText(entry.getKey(), entry.getValue());
        }
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

    private void removeTextAnimation() {
        new Thread(() -> {
            while (question.getText().toString().length() > 1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                question.post(() -> question.setText(question.getText().toString().substring(0, question.getText().toString().length() - 1)));
                option_1.post(() -> {
                    if (option_1.getText().toString().length() > 0)
                        option_1.setText(option_1.getText().toString().substring(0, option_1.getText().toString().length() - 1));
                });
                option_2.post(() -> {
                    if (option_2.getText().toString().length() > 0)
                        option_2.setText(option_2.getText().toString().substring(0, option_2.getText().toString().length() - 1));
                });
                option_3.post(() -> {
                    if (option_3.getText().toString().length() > 0)
                        option_3.setText(option_3.getText().toString().substring(0, option_3.getText().toString().length() - 1));
                });
                option_4.post(() -> {
                    if (option_4.getText().toString().length() > 0)
                        option_4.setText(option_4.getText().toString().substring(0, option_4.getText().toString().length() - 1));
                });
            }
            runQuestion();
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void countDown(final TextView view) {
        timer.setMax(100000);
        this.runOnUiThread(() -> {
            ObjectAnimator animation = ObjectAnimator.ofInt(timer, "progress", 100 * 1000, 0);
            animation.setDuration(10000);
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
        });
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
            removeTextAnimation();
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void animateText(String key, ArrayList<String> value) {
        final char[] q = key.toCharArray();
        final char[] o1 = value.get(0).toCharArray();
        final char[] o2 = value.get(1).toCharArray();
        final char[] o3 = value.get(2).toCharArray();
        final char[] o4 = value.get(3).toCharArray();
        new Thread(() -> {
            this.runOnUiThread(() -> {
                ObjectAnimator animation = ObjectAnimator.ofInt(timer, "progress", 0, 100 * 1000);
                animation.setDuration(1000);
                animation.setInterpolator(new LinearInterpolator());
                animation.start();
                time.setText("10");
            });
            for (int i = 0; i < q.length; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int finalI = i;
                question.post(() -> question.append("" + q[finalI]));
                option_1.post(() -> {
                    if (o1.length > finalI) option_1.append("" + o1[finalI]);
                });
                option_2.post(() -> {
                    if (o2.length > finalI) option_2.append("" + o2[finalI]);
                });
                option_3.post(() -> {
                    if (o3.length > finalI) option_3.append("" + o3[finalI]);
                });
                option_4.post(() -> {
                    if (o4.length > finalI) option_4.append("" + o4[finalI]);
                });
            }
            countDown(time);
        }).start();
    }
}