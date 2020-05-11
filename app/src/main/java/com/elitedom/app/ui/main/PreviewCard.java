package com.elitedom.app.ui.main;

class PreviewCard {
    private final int imageResource;
    private String title;
    private String subtext;

    PreviewCard(String title, String subtext) {
        this.title = title;
        this.subtext = subtext;
        this.imageResource = 1;
    }

    String getTitle() {
        return title;
    }

    String getSubtext() {
        return subtext;
    }

    int getImageResource() { return imageResource; }

}
