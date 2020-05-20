package com.elitedom.app.ui.aux;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.elitedom.app.R;

import java.util.Objects;

public class user_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        

    }
}
