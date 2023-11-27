package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(MethodArgumentNotValidException e) {
        String errorMessage = e.getMessage();
        log.error("Method Argument Not Valid Exception = {}", errorMessage);
        return new ErrorResponse("MethodArgumentNotValidException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String errorMessage = e.getMessage();
        log.error("Missing Request Header Exception = {}", errorMessage);
        return new ErrorResponse("MissingRequestHeaderException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = e.getMessage();
        log.error("Http Message Not Readable Exception = {}", errorMessage);
        return new ErrorResponse("HttpMessageNotReadableException", errorMessage);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidateException(NotFoundException e) {
        String errorMessage = e.getMessage();
        log.error("NotFoundException = {}", errorMessage);
        return new ErrorResponse("NotFoundException", errorMessage);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(ValidateException e) {
        String errorMessage = e.getMessage();
        log.error("Validate Exception = {}", errorMessage);
        return new ErrorResponse("ValidateException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        String errorMessage = e.getMessage();
        log.error("Already Exists Exception = {}", errorMessage);
        return new ErrorResponse("EmailAlreadyExistsException", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUndefinedException(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMessage = e.getMessage();
        String stackTrace = stringWriter.toString();
        log.error("Exception = {}", errorMessage, e);
        return new ErrorResponse(e.toString(), errorMessage, stackTrace);
    }
}
