// src/main/java/com/dashboard/api/exception/UserNotFoundException.java
package com.dashboard.api.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}