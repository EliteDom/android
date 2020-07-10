package com.elitedom.app.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elitedom.app.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PasswordReset extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mEmail = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        final ConstraintLayout constraintLayout = findViewById(R.id.reset_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @SuppressLint("InflateParams")
    public void resetPassword(View view) {
        if (mEmail.getText().toString().length() > 0) {
            mAuth.sendPasswordResetEmail(mEmail.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            Snackbar.make(findViewById(R.id.reset_layout), "Check your inbox for password reset details!", Snackbar.LENGTH_SHORT)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                    .setAnchorView(LayoutInflater.from(this).inflate(R.layout.activity_login, null).findViewById(R.id.sign_up_window))
                                    .show();
                    });
            startActivity(new Intent(this, LoginActivity.class));
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
