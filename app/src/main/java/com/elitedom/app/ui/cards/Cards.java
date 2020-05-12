package com.elitedom.app.ui.cards;

import android.net.Uri;

class Cards {
    private String title;
    private String info;
    private final Uri imageResource;

    Cards(String title, String info, Uri imageResource) {
        this.title = title;
        this.info = info;
        this.imageResource = imageResource;
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