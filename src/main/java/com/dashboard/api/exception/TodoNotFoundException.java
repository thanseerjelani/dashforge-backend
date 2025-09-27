// src/main/java/com/dashboard/api/exception/TodoNotFoundException.java
package com.dashboard.api.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(String message) {
        super(message);
    }
}