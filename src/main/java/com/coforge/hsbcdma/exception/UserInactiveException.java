package com.coforge.hsbcdma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Created by Chetan
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
}
