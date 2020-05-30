package com.elitedom.app.ui.messaging;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileMessaging extends AppCompatActivity {

    private EditText message;
    private String uid, dorm;
    private FirebaseFirestore mDatabase;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_messaging);

        RecyclerView mRecyclerView = findViewById(R.id.messageList);
        mDatabase = FirebaseFirestore.getInstance();
        message = findViewById(R.id.edittext_chatbox);
        uid = getIntent().getStringExtra("uid");
        dorm = getIntent().getStringExtra("dorm");
        messageArrayList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, messageArrayList);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        TextView mUsername = findViewById(R.id.username);
        mUsername.setText(getIntent().getStringExtra("username"));

        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    private void initializeData() {
        messageArrayList.clear();
        mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.get("sender") != null) messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp"), (String) document.get("sender"), Uri.parse((String) document.get("image"))));
                                else messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp")));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {

    }
}
