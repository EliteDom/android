package com.elitedom.app.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.elitedom.app.R;
import com.elitedom.app.ui.cards.TopicCards;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        final Button passwordReset = findViewById(R.id.forgot_password);
        constraintLayout = findViewById(R.id.login_layout);
        TextView title = findViewById(R.id.title);

        Animation atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        Animation atg2 = AnimationUtils.loadAnimation(this, R.anim.atg2);
        Animation atg3 = AnimationUtils.loadAnimation(this, R.anim.atg3);
        mAuth = FirebaseAuth.getInstance();

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        emailEditText.startAnimation(atg);
        passwordEditText.startAnimation(atg2);
        loginButton.startAnimation(atg3);
        passwordReset.startAnimation(atg3);
        animateText(title);
    }

    private void animateText(final TextView view) {
        final char[] s = "Elitedom".toCharArray();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            public void run() {
                for (final char ch : s) {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    view.post(new Runnable() {
                        public void run() {
                            view.append("" + ch);
                        }
                    });
                }
            }
        }).start();
    }

    private void addUser(String email, String password) {
        Toast.makeText(getApplicationContext(), "Creating new user", Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            Intent mainscreen = new Intent(LoginActivity.this, NewUser.class);
                            startActivity(mainscreen);
                            overridePendingTransition(0, 0);
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                        else Toast.makeText(getApplicationContext(), "Unsuccessful - try again later?", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void topicActivity(FirebaseUser user) {
        if (user != null) {
            Intent mainscreen = new Intent(this, TopicCards.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, constraintLayout, "gradientShift");
            ActivityCompat.startActivity(this, mainscreen, options.toBundle());
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public void loginButton(View view) {
        Toast.makeText(getApplicationContext(), "Logging you in", Toast.LENGTH_LONG).show();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        (mAuth.signInWithEmailAndPassword(email, password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) topicActivity(mAuth.getCurrentUser());
                        else addUser(email, password);
                    }
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
