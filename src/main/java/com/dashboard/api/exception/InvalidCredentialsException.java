// src/main/java/com/dashboard/api/exception/InvalidCredentialsException.java
package com.dashboard.api.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}