package com.parasme.swopinfo;

/**
 * Created by SoNu on 3/3/2017.
 */

public class HelloWorldEvent {
    private final String message;

    public HelloWorldEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}