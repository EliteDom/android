package com.elitedom.app.ui.messaging;

import android.net.Uri;

class Message {
    private Uri imageResource;
    private String message, timestamp, sender;
    private int multipleFlag;

    Message(String message, String timestamp, String sender, Uri imageResource, int multipleFlag) {
        this.message = message;
        this.timestamp = timestamp;
        this.imageResource = imageResource;
        this.multipleFlag = multipleFlag;
        this.sender = sender;
    }

    Message(String message, String timestamp, int multipleFlag) {
        this.message = message;
        this.timestamp = timestamp;
        this.multipleFlag = multipleFlag;
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

    int getMultipleFlag() {
        return multipleFlag;
    }

    Uri getImageResource() {
        return imageResource;
    }
}
