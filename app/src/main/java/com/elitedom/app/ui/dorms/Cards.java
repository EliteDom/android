package com.elitedom.app.ui.dorms;

import android.net.Uri;

class Cards {
    private final Uri imageResource;
    private String title;
    private String info;

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