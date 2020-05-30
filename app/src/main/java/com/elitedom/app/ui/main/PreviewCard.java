package com.elitedom.app.ui.main;

import android.net.Uri;

public class PreviewCard {
    private final Uri imageResource;
    private String title, subtext, author, uid, dorm;

    public PreviewCard(String title, String subtext, String author, String uid, String dorm, Uri imageResource) {
        this.title = title;
        this.subtext = subtext;
        this.author = author;
        this.uid = uid;
        this.dorm = dorm;
        this.imageResource = imageResource;
    }

    String getTitle() {
        return title;
    }

    String getSubtext() {
        return subtext;
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
