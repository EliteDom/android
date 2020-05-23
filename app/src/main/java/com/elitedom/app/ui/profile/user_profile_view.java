package com.elitedom.app.ui.profile;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.elitedom.app.ui.main.PostView;

import java.util.Objects;

public class user_profile_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        TextView mPostTitle = findViewById(R.id.title);
        ImageView mPostImage = findViewById(R.id.postImage);
        TextView mPostText = findViewById(R.id.post_text);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent intent = getIntent();
//        mPostTitle.setText(intent.getStringExtra("title"));
//        mPostText.setText(intent.getStringExtra("subtext"));
//        Glide.with(this)
//                .load(intent.getStringExtra("image"))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(mPostImage);

//        mPostImage.setClipToOutline(true);
//        mPostText.setClipToOutline(true);
    }

    public void backAction(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    public void sharedexpansion(View view) {
        Intent intent = new Intent(this, user_profile.class);
        Pair<View, String> t1 = Pair.create(findViewById(R.id.profile_image), "image");
        Pair<View, String> t2 = Pair.create(findViewById(R.id.username), "username");
        Pair<View, String> t3 = Pair.create(findViewById(R.id.user_profile_holder), "post_cards");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, t1, t2);
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation(this, findViewById(R.id.profile_image), "image");
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}

/*
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/user_profile_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="12dp"
    android:orientation="vertical"
    android:transitionName="postview_expansion"
    tools:context=".ui.profile.user_profile_view">

    <RelativeLayout
        android:id="@+id/profile_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="12dp"
        android:gravity="center">

        <de.hdodenhof.circleimageview.CircleImageView
            android:id="@+id/profile_image"
            android:layout_width="36dp"
            android:layout_height="36dp"
            android:src="@drawable/wp_linux"
            app:civ_border_color="@color/colorAccent"
            app:civ_border_width="4dp" />

        <TextView
            android:id="@+id/username"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="4dp"
            android:layout_toEndOf="@id/profile_image"
            android:text="@string/jinen_setpal"
            android:textColor="@color/black"
            android:textSize="24sp"
            android:textStyle="bold" />
    </RelativeLayout>

    <androidx.cardview.widget.CardView
        android:id="@+id/user_profile_holder"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        app:cardCornerRadius="16dp"
        app:cardElevation="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="@id/profile_data">

        <RelativeLayout
            android:id="@+id/user_feed_container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:animateLayoutChanges="true"
            android:background="@drawable/gradient_list"
            android:paddingTop="4dp"
            android:paddingBottom="@dimen/activity_vertical_margin"
            tools:context="com.example.android.materialme.MainActivity">

            <com.google.android.material.floatingactionbutton.FloatingActionButton
                android:id="@+id/fab"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_alignParentEnd="true"
                android:layout_alignParentBottom="true"
                android:layout_marginEnd="32dp"
                android:layout_marginBottom="32dp"
                android:src="@drawable/ic_arrow_back_white_24dp" />

            <androidx.cardview.widget.CardView
                android:id="@+id/post_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_margin="16dp"
                android:onClick="backAction"
                app:cardCornerRadius="8dp"
                app:cardElevation="0dp">

                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                    <TextView
                        android:id="@+id/title"
                        style="@style/TextAppearance.AppCompat.Headline"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:padding="8dp"
                        android:textColor="@color/black"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:theme="@style/ThemeOverlay.AppCompat.Dark" />

                    <androidx.cardview.widget.CardView
                        android:id="@+id/action_cards"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_below="@id/postImage"
                        android:layout_marginHorizontal="6dp"
                        app:cardBackgroundColor="@color/colorAccent"
                        app:cardCornerRadius="8dp"
                        app:cardElevation="0dp">

                        <LinearLayout
                            android:id="@+id/action_icons"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:layout_margin="6dp"
                            android:orientation="horizontal">

                            <ImageView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_weight="0.5"
                                android:src="@drawable/ic_arrow_back_white_24dp"
                                tools:ignore="ContentDescription" />

                            <ImageView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_weight="0.5"
                                android:src="@drawable/ic_arrow_back_white_24dp"
                                tools:ignore="ContentDescription" />

                            <ImageView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_weight="0.5"
                                android:src="@drawable/ic_arrow_back_white_24dp"
                                tools:ignore="ContentDescription" />

                            <ImageView
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_weight="0.5"
                                android:src="@drawable/ic_arrow_back_white_24dp"
                                tools:ignore="ContentDescription" />

                        </LinearLayout>
                    </androidx.cardview.widget.CardView>

                    <ImageView
                        android:id="@+id/postImage"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_below="@id/title"
                        android:layout_marginHorizontal="6dp"
                        android:layout_marginBottom="6dp"
                        android:adjustViewBounds="true"
                        android:background="@drawable/rounded_image"
                        tools:ignore="ContentDescription" />

                    <ScrollView
                        android:id="@+id/text_scroll"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:layout_below="@id/action_cards"
                        android:background="@drawable/rounded_image"
                        android:scrollbars="none">

                        <TextView
                            android:id="@+id/post_text"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:layout_marginStart="8dp"
                            android:layout_marginEnd="8dp"
                            android:background="@drawable/rounded_image"
                            android:textColor="@color/black"
                            android:textSize="14sp"
                            android:textStyle="bold" />
                    </ScrollView>

                </RelativeLayout>
            </androidx.cardview.widget.CardView>
        </RelativeLayout>
    </androidx.cardview.widget.CardView>
</LinearLayout>

 */