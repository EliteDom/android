package com.nthusiast.app.ui.communities;

import android.net.Uri;

class Card {
    private final Uri imageResource;
    private String title;
    private String info;

    Card(String title, String info, Uri imageResource) {
        this.title = title;
        this.info = info;
        this.imageResource = imageResource;
    }

    Card(String text) {
        this.title = text;
        this.imageResource = Uri.parse("");
    }

    String getTitle() {
        return title;
    }

    String getInfo() {
        return info;
    }

    Uri getImageResource() {
        return imageResource;
    }
}