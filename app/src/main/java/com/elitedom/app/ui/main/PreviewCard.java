package com.elitedom.app.ui.main;

import android.net.Uri;

class PreviewCard {
    private final Uri imageResource;
    private String title;
    private String subtext;

    PreviewCard(String title, String subtext, Uri imageResource) {
        this.title = title;
        this.subtext = subtext;
        this.imageResource = imageResource;
    }

    String getTitle() {
        return title;
    }

    String getSubtext() {
        return subtext;
    }

    Uri getImageResource() { return imageResource; }

}
