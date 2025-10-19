// src/main/java/com/dashboard/api/exception/InvalidTokenException.java
package com.dashboard.api.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}