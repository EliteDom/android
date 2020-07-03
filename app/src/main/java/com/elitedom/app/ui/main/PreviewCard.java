package com.elitedom.app.ui.main;

import android.net.Uri;

public class PreviewCard {
    private final Uri imageResource;
    private String title, body, author, uid, dorm;

    public PreviewCard(String title, String body, String author, String uid, String dorm, Uri imageResource) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.uid = uid;
        this.dorm = dorm;
        this.imageResource = imageResource;
    }

    public PreviewCard(Uri imageResource) {
        this.imageResource = imageResource;
    }

    String getTitle() {
        return title;
    }

    String getBody() {
        return body;
    }

    String getAuthor() {
        return author;
    }

    String getUid() {
        return uid;
    }

    String getDorm() {
        return dorm;
    }

    Uri getImageResource() {
        return imageResource;
    }

}
