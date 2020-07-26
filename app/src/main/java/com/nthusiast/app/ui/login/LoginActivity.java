package com.nthusiast.app.ui.login;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nthusiast.app.R;
import com.nthusiast.app.ui.communities.TopicCards;

import java.util.Objects;
import java.util.regex.Pattern;

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

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        TextView title = findViewById(R.id.title);
        Button loginButton = findViewById(R.id.login);
        passwordEditText = findViewById(R.id.password);
        constraintLayout = findViewById(R.id.login_layout);
        CardView emailCard = findViewById(R.id.email_card);
        CardView passwordCard = findViewById(R.id.password_card);
        final Button passwordReset = findViewById(R.id.forgot_password);

        Animation atg1 = AnimationUtils.loadAnimation(this, R.anim.atg);
        Animation atg2 = AnimationUtils.loadAnimation(this, R.anim.atg2);
        Animation atg3 = AnimationUtils.loadAnimation(this, R.anim.atg3);
        emailCard.startAnimation(atg1);
        passwordCard.startAnimation(atg2);
        loginButton.startAnimation(atg3);
        passwordReset.startAnimation(atg3);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        animateText(title);
    }

    private void animateText(final TextView view) {
        final char[] s = "Nthusiast".toCharArray();
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
        Snackbar.make(constraintLayout, "Creating new user ", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAnchorView(R.id.sign_up_window)
                .show();
        Intent mainscreen = new Intent(LoginActivity.this, NewUser.class);
        mainscreen.putExtra("email", email);
        mainscreen.putExtra("password", password);
        startActivity(mainscreen);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        setResult(Activity.RESULT_OK);
    }

    private void topicActivity(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, TopicCards.class));
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    public void loginButton(View view) {
        Snackbar.make(constraintLayout, "Logging you in", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAnchorView(R.id.sign_up_window)
                .show();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (emailEditText.getText().toString().length() == 0 || passwordEditText.getText().toString().length() == 0)
            Snackbar.make(constraintLayout, "Fill both options!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(R.id.sign_up_window)
                    .show();
        else if (!isValid(email))
            Snackbar.make(constraintLayout, "Invalid email!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(R.id.sign_up_window)
                    .show();
        else
            (mAuth.signInWithEmailAndPassword(email, password))
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) topicActivity(mAuth.getCurrentUser());
                        else
                            mAuth.fetchSignInMethodsForEmail(email)
                                    .addOnCompleteListener(task1 -> {
                                        if (Objects.requireNonNull(Objects.requireNonNull(task1.getResult()).getSignInMethods()).isEmpty())
                                            addUser(email, password);
                                        else
                                            Snackbar.make(constraintLayout, "Incorrect Password! ", Snackbar.LENGTH_LONG)
                                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                                    .setAnchorView(R.id.sign_up_window)
                                                    .show();
                                    });
                    });
    }

    public void resetPassword(View view) {
        Intent forgotIntent = new Intent(this, PasswordReset.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, constraintLayout, "gradientShift");
        ActivityCompat.startActivity(this, forgotIntent, options.toBundle());
        setResult(Activity.RESULT_OK);
    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}