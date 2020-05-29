package com.elitedom.app.ui.main;

import android.net.Uri;

public class PreviewCard {
    private final Uri imageResource;
    private String title, subtext, author;

    public PreviewCard(String title, String subtext, String author, Uri imageResource) {
        this.title = title;
        this.subtext = subtext;
        this.author = author;
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

    Uri getImageResource() {
        return imageResource;
    }

}
