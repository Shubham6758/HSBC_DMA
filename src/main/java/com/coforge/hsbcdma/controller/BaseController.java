package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.domain.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * This is a base controller with common functionality.
 * Created By : pratish.b
 */

public abstract class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected <T> ResponseEntity<ApiResponse<T>> success(T data){
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> success(String message, T data){
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> create(T data){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Created successfully",data));
    }

    protected <T> ResponseEntity<ApiResponse<?>> error(String message, HttpStatus status){
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message));
    }

    protected ResponseEntity<ApiResponse<?>> badRequest(String message){
        return error(message, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<ApiResponse<?>> notFound(String message){
        return error(message, HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<ApiResponse<?>> serverError(String message){
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
