// src/main/java/com/dashboard/api/exception/ValidationException.java
package com.dashboard.api.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}