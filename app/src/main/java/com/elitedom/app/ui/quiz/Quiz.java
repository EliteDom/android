package com.elitedom.app.ui.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elitedom.app.R;

import java.util.Objects;

public class Quiz extends AppCompatActivity {

    private TextView time, question, option_1, option_2, option_3, option_4;

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

        time = findViewById(R.id.time);
        question = findViewById(R.id.question);
        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        option_3 = findViewById(R.id.option_3);
        option_4 = findViewById(R.id.option_4);

        ProgressBar timer = findViewById(R.id.timer);
        timer.setMax(100000);

        ObjectAnimator animation = ObjectAnimator.ofInt(timer, "progress", 100 * 1000, 0);
        animation.setDuration(10000);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
        countDown(time);
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

    private void countDown(final TextView view) {
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