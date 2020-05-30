package com.elitedom.app.ui.messaging;

import android.net.Uri;

class Message {
    private Uri imageResource;
    private String message, timestamp, sender;

    Message(String message, String timestamp, String sender, Uri imageResource) {
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.imageResource = imageResource;
    }

    Message(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    String getMessage() {
        return message;
    }

    String getSender() {
        return sender;
    }

    String getTimestamp() {
        return timestamp;
    }

    Uri getImageResource() {
        return imageResource;
    }
}
