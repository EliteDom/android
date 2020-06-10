package com.elitedom.app.ui.messaging;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FeedMessaging extends AppCompatActivity {

    String dorm, uid, authorImage;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter mAdapter;
    private FirebaseFirestore mDatabase;
    private EditText message;
    private TextView mNoMessages;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_messaging);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mRecyclerView = findViewById(R.id.messageList);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = getIntent().getStringExtra("uid");
        dorm = getIntent().getStringExtra("dorm");
        message = findViewById(R.id.edittext_chatbox);
        mNoMessages = findViewById(R.id.no_messages);
        mDatabase = FirebaseFirestore.getInstance();
        messageArrayList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, messageArrayList);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();

        message.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollToBottom();
            }
        });
    }

    private int returnFlagRes(String prev_author, String cur_author) {
        if (prev_author.equals(cur_author)) return 0;
        else return 1;
    }

    private void initializeData() {
        final String[] flag = {""};
        final CollectionReference chatRef = mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats");
        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    messageArrayList.clear();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                        if (document != null) {
                            if (document.get("sender") != null && !Objects.requireNonNull(document.get("sender")).toString().equals(FirebaseAuth.getInstance().getUid()))
                                messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp"), (String) document.get("sender"), Uri.parse((String) document.get("image")), returnFlagRes(Objects.requireNonNull(flag[0]), (String) document.get("sender"))));
                            else
                                messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp"), returnFlagRes(Objects.requireNonNull(flag[0]), (String) document.get("sender"))));
                            flag[0] = (String) document.get("sender");
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 0) scrollToBottom();
                    else mNoMessages.animate().alpha(1.0f);
                }
            }
        });

        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            authorImage = (String) document.get("image");
                            if (authorImage == null) authorImage = "";
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {
        if (message.getText().toString().length() > 0) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<String, Object> messageBlock = new HashMap<>();
            String date = getDate(timestamp.toString());
            messageBlock.put("message", message.getText().toString());
            messageBlock.put("timestamp", date);
            messageBlock.put("sender", auth.getUid());
            messageBlock.put("image", authorImage);
            mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats").document(String.valueOf(timestamp.getTime()))
                    .set(messageBlock);
            message.setText("");
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() >= 0) mNoMessages.animate().alpha(0.0f);
        } else
            Toast.makeText(getApplicationContext(), "No message body!", Toast.LENGTH_SHORT).show();
    }

    private String getDate(String time) {
        StringBuilder res = new StringBuilder();
        for (char ch : time.toCharArray()) {
            if (ch == ' ' || res.length() > 0) res.append(ch);
            if (res.length() > 5) break;
        }
        return res.toString();
    }

    private void scrollToBottom() {
        mNoMessages.animate().alpha(0.0f);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }
}