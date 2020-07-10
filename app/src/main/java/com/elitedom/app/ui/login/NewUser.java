package com.elitedom.app.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.elitedom.app.R;
import com.elitedom.app.ui.cards.TopicCards;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUser extends AppCompatActivity {

    private EditText mUsername, mFirstName, mLastName;
    private static final int SELECT_PICTURE = 1;
    private FirebaseFirestore mDatabase;
    private CircleImageView imageView;
    private StorageReference mStorage;
    private Uri localUri, downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        ConstraintLayout constraintLayout = findViewById(R.id.new_user_layout);
        mUsername = findViewById(R.id.username);
        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.profile_image);

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
            userData.put("appreciationPoints", 0);
            userData.put("predictorPoints", 0);

            mDatabase.collection("users")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .set(userData);

            Intent intent = new Intent(this, TopicCards.class);
            intent.putExtra("image", localUri.toString());
            uploadImage();
            startActivity(intent);
            setResult(Activity.RESULT_OK);
            finish();
        } else
            Toast.makeText(getApplicationContext(), "Please fill every field!", Toast.LENGTH_SHORT).show();
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
            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Update Profile")
                    .setMessage("Change your profile picture?")
                    .setPositiveButton("Update", (dialogInterface, i) -> {})
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
            if (task.isSuccessful()) {
                mDatabase.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .update("image", Objects.requireNonNull(task.getResult()).toString());
                Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getUid(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
