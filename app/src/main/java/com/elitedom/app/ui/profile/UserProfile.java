package com.elitedom.app.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.elitedom.app.ui.main.PreviewAdapter;
import com.elitedom.app.ui.main.PreviewCard;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private TextView username, appreciation, predictor;
    private static final int SELECT_PICTURE = 1;
    private ArrayList<PreviewCard> mTitleData;
    private FirebaseFirestore mDatabase;
    private StorageReference mStorage;
    private CircleImageView imageView;
    private PreviewAdapter mAdapter;
    private RecyclerView mRecycler;
    private TextView mNoPosts;
    private Uri localUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        mDatabase = FirebaseFirestore.getInstance();
        username = findViewById(R.id.username);
        appreciation = findViewById(R.id.appreciation_score);
        predictor = findViewById(R.id.predictor_score);
        imageView = findViewById(R.id.profile_image);
        mNoPosts = findViewById(R.id.no_posts);
        mStorage = FirebaseStorage.getInstance().getReference();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mRecycler = findViewById(R.id.recyclerView);
        mRecycler.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecycler.setClipToOutline(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mTitleData = new ArrayList<>();
        mAdapter = new PreviewAdapter(this, mTitleData, "post_expansion", 2);
        mAdapter.sendContext(imageView, username, findViewById(R.id.user_profile_holder));
        mRecycler.setAdapter(mAdapter);

        initializeData();
    }

    @SuppressLint("SetTextI18n")
    private void initializeData() {
        mTitleData.clear();
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        predictor.setText(Objects.requireNonNull(document.get("predictorPoints")).toString());
                        appreciation.setText(Objects.requireNonNull(document.get("appreciationPoints")).toString());
                        username.setText(Objects.requireNonNull(document.get("firstName")).toString() + " " + Objects.requireNonNull(document.get("lastName")).toString() + "'s Profile");
                        String image = (String) document.get("image");
                        if (image != null && image.length() > 0)
                            Glide.with(UserProfile.this)
                                    .load(image)
                                    .fitCenter()
                                    .into(imageView);
                        imageView.setContentDescription(image);
                        mTitleData.add(new PreviewCard(Uri.parse(image)));
                    }
                });
        mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("authoredPosts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            mDatabase.collection("dorms").document((String) Objects.requireNonNull(document.get("dormName"))).collection("posts").document((String) Objects.requireNonNull(document.get("postId")))
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document1 = task1.getResult();
                                            assert document1 != null;
                                            if (document1.get("image") != null)
                                                mTitleData.add(new PreviewCard((String) document1.get("title"), (String) document1.get("postText"), document1.get("author") + " | Authored " + document1.get("timestamp") + " ago", document1.getId(), (String) document.get("dormName"), Uri.parse((String) document1.get("image"))));
                                            else
                                                mTitleData.add(new PreviewCard((String) document1.get("title"), (String) document1.get("postText"), document1.get("author") + " | Authored " + document1.get("timestamp") + " ago", document1.getId(), (String) document.get("dormName")));
                                            mAdapter.notifyDataSetChanged();
                                            if (mAdapter.getItemCount() > 0)
                                                runLayoutAnimation(mRecycler);
                                            else mNoPosts.animate().alpha(1.0f);
                                        }
                                    });
                        }
                });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_topic_cards_animation);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @SuppressLint("IntentReset")
    public void newProfileImage(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            localUri = data.getData();
            Glide.with(this)
                    .load(localUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(imageView);
            mAdapter.notifyDataSetChanged();
            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Update Profile")
                    .setMessage("Change your profile picture??")
                    .setPositiveButton("Replace", (dialogInterface, i) -> uploadImage())
                    .setNeutralButton("Retain", (dialogInterface, i) -> retainProfilePicture())
                    .show();
        }
    }

    @SuppressLint("InflateParams")
    private void retainProfilePicture() {
        Glide.with(this)
                .load(imageView.getContentDescription().toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageView);
        mAdapter.notifyDataSetChanged();
    }

    private void uploadImage() {
        final StorageReference ref = mStorage.child("profiles/" + UUID.randomUUID().toString() + ".jpg");
        UploadTask uploadTask = ref.putFile(localUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .update("image", Objects.requireNonNull(task.getResult()).toString());
        });
    }
}
