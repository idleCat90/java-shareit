package ru.practicum.shareit.exception;

public class NoUserBookingsException extends RuntimeException {
    public NoUserBookingsException(String message) {
        super(message);
    }

}
