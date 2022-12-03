package com.konstde00.commons.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@ResponseStatus(value = METHOD_NOT_ALLOWED)
public class NotValidException extends RuntimeException {

    public NotValidException(String message) {
        super(message);
    }
}
