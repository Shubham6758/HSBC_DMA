package com.coforge.hsbcdma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DMAGenericRuntimeException extends RuntimeException{

    public DMAGenericRuntimeException() {
        super("Required Data is missing.");
    }
    public DMAGenericRuntimeException(String message) {
        super(message);
    }
}
