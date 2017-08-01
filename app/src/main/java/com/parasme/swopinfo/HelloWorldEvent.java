package com.parasme.swopinfo;

/**
 * Created by Mukesh Kumawat on 3/3/2017.
 */

public class HelloWorldEvent {
    private final String message;
    private String temp;
    public HelloWorldEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}