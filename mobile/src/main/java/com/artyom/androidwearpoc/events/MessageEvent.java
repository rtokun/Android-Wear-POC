package com.artyom.androidwearpoc.events;

/**
 * Created by Artyom on 12/01/2017.
 */

public class MessageEvent {

    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
