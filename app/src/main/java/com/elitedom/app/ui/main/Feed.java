package com.elitedom.app.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.elitedom.app.R;
import com.elitedom.app.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent login = new Intent(Feed.this, LoginActivity.class);
        startActivity(login);
    }
}
