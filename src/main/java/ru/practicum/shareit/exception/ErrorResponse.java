package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String error;
    private final String message;
    private String stackTraceElement;

    public ErrorResponse(String error, String message, String stackTraceElement) {
        this.error = error;
        this.message = message;
        this.stackTraceElement = stackTraceElement;
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public void setStackTraceElement(String stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }
}