package com.elitedom.app.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.elitedom.app.R;
import com.elitedom.app.ui.cards.TopicCards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;
    private EditText emailEditText, passwordEditText;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        topicActivity(currentUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button loginButton = findViewById(R.id.login);
        final Button passwordReset = findViewById(R.id.forgot_password);
        TextView title = findViewById(R.id.title);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        constraintLayout = findViewById(R.id.login_layout);
        mAuth = FirebaseAuth.getInstance();

        Animation atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        Animation atg2 = AnimationUtils.loadAnimation(this, R.anim.atg2);
        Animation atg3 = AnimationUtils.loadAnimation(this, R.anim.atg3);
        emailEditText.startAnimation(atg);
        passwordEditText.startAnimation(atg2);
        loginButton.startAnimation(atg3);
        passwordReset.startAnimation(atg3);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        animateText(title);
    }

    private void animateText(final TextView view) {
        final char[] s = "Elitedom".toCharArray();
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

    private void addUser(String email, String password) {
        Toast.makeText(getApplicationContext(), "Creating new user", Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        Intent mainscreen = new Intent(LoginActivity.this, NewUser.class);
                        startActivity(mainscreen);
                        overridePendingTransition(0, 0);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Unsuccessful - try again later?", Toast.LENGTH_LONG).show();
                });
    }

    private void topicActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, TopicCards.class));
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public void loginButton(View view) {
        Toast.makeText(getApplicationContext(), "Logging you in", Toast.LENGTH_LONG).show();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        (mAuth.signInWithEmailAndPassword(email, password))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) topicActivity(mAuth.getCurrentUser());
                    else
                        mAuth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener(task1 -> {
                                    if (Objects.requireNonNull(Objects.requireNonNull(task1.getResult()).getSignInMethods()).isEmpty())
                                        addUser(email, password);
                                    else
                                        Toast.makeText(getApplicationContext(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
                                });
                });
    }

    public void resetPassword(View view) {
        Intent forgotIntent = new Intent(this, PasswordReset.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, constraintLayout, "gradientShift");
        ActivityCompat.startActivity(this, forgotIntent, options.toBundle());
        setResult(Activity.RESULT_OK);
    }
}

/*    private void removeTextAnimation(final EditText txt_view) {
        new Thread(new Runnable() {
            public void run() {
                while (txt_view.getText().toString().length() > 1) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    txt_view.post(new Runnable() {
                        public void run() {
                            txt_view.setText(txt_view.getText().toString().substring(0, txt_view.getText().toString().length() - 1));
                        }
                    });
                }
            }
        }).start();
    }*/