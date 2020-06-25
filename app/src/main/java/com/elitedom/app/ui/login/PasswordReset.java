package com.elitedom.app.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    public void resetPassword(View view) {
        mAuth.sendPasswordResetEmail(mEmail.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Toast.makeText(getApplicationContext(), "Check your inbox for password reset details!", Toast.LENGTH_LONG).show();
                });
        startActivity(new Intent(this, LoginActivity.class));
        setResult(Activity.RESULT_OK);
        finish();
    }
}
