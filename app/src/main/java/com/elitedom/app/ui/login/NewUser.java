package com.elitedom.app.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elitedom.app.R;
import com.elitedom.app.ui.cards.TopicCards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewUser extends AppCompatActivity {

    private EditText mUsername, mFirstName, mLastName;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        ConstraintLayout constraintLayout = findViewById(R.id.new_user_layout);
        mUsername = findViewById(R.id.username);
        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mDatabase = FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public void submitData(View view) {
        Map<String, Object> userData = new HashMap<>();
        String username = mUsername.getText().toString();
        String first = mFirstName.getText().toString();
        String last = mLastName.getText().toString();

        if (username.length() > 0 && first.length() > 0 && last.length() > 0) {
            userData.put("firstName", mFirstName.getText().toString());
            userData.put("lastName", mLastName.getText().toString());
            userData.put("username", mUsername.getText().toString());
            userData.put("posts", null);
            userData.put("appreciationPoints", 0);
            userData.put("predictorPoints", 0);

            mDatabase.collection("users")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .set(userData);

            startActivity(new Intent(this, TopicCards.class));
            setResult(Activity.RESULT_OK);
            finish();
        } else
            Toast.makeText(getApplicationContext(), "Please fill every field!", Toast.LENGTH_SHORT).show();
    }
}
