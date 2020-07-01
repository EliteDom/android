package com.elitedom.app.ui.messaging;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elitedom.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMessaging extends AppCompatActivity {

    private EditText message;
    private TextView mNoMessages;
    private MessageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mDatabase;
    private String uid, dorm, authorImage;
    private ArrayList<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_messaging);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        TextView mUsername = findViewById(R.id.username);
        mUsername.setText(getIntent().getStringExtra("username"));

        mRecyclerView = findViewById(R.id.messageList);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = getIntent().getStringExtra("uid");
        dorm = getIntent().getStringExtra("dorm");
        message = findViewById(R.id.edittext_chatbox);
        messageArrayList = new ArrayList<>();
        mNoMessages = findViewById(R.id.no_messages);
        mAdapter = new MessageAdapter(this, messageArrayList);
        mDatabase = FirebaseFirestore.getInstance();
        CircleImageView mProfileImage = findViewById(R.id.profile_image);

        Glide.with(this)
                .load(getIntent().getStringExtra("profileImage"))
                .into(mProfileImage);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    private int returnFlagRes(String prev_author, String cur_author) {
        if (prev_author.equals(cur_author)) return 0;
        else return 1;
    }

    private void initializeData() {
        final String[] flag = {""};
        final CollectionReference chatRef = mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats");
        chatRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                messageArrayList.clear();
                for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                    if (document != null) {
                        if (document.get("message") == null) messageArrayList.add(new Message((String) document.get("timestamp")));
                        else if (!Objects.requireNonNull(document.get("sender")).toString().equals(FirebaseAuth.getInstance().getUid()))
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
        });

        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        authorImage = (String) document.get("image");
                        if (authorImage == null) authorImage = "";
                    }
                });

        message.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToBottom());
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {
        if (validInput(message.getText().toString())) {
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
            Toast.makeText(getApplicationContext(), "Invalid message body!", Toast.LENGTH_SHORT).show();
    }

    private boolean validInput(String message) {
        if (message.length() == 0) return false;
        for(int i = 0; i < message.length(); i++) if (message.charAt(i) != ' ') return true;
        return false;
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
        if (mAdapter.getItemCount() > 0) {
            mNoMessages.animate().alpha(0.0f);
            mRecyclerView.post(() -> mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1));
        }
    }
}