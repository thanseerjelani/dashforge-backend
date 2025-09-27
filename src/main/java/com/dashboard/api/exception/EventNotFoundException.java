// src/main/java/com/dashboard/api/exception/EventNotFoundException.java
package com.dashboard.api.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}