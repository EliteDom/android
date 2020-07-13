package com.elitedom.app.ui.search;

import android.net.Uri;

public class Result {
    private final Uri imageResource;
    private String name;

    Result(String name, Uri imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    String getName() {
        return name;
    }

    Uri getImageResource() {
        return imageResource;
    }
}
