// src/main/java/com/dashboard/api/exception/UserAlreadyExistsException.java
package com.dashboard.api.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}