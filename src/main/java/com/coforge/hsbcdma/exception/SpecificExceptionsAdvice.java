package com.coforge.hsbcdma.exception;


import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SpecificExceptionsAdvice {

    private static final Logger logger = LoggerFactory.getLogger(SpecificExceptionsAdvice.class);

    /**
     * This method handles validation error by handling MethodArgumentNotValidException.
     * Created By : pratish.b
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse body = new ErrorResponse();
        body.setCode("VALIDATION_ERROR");
        body.setMessage("Validation failed");
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        body.setPath(request.getDescription(false).replace("uri=", ""));
        body.setTimestamp(OffsetDateTime.now());
        body.setErrors(ex.getBindingResult().getFieldErrors().stream().map(fe -> {
            ErrorResponse.FieldErrorItem item = new ErrorResponse.FieldErrorItem();
            item.setField(fe.getField());
            item.setError(fe.getDefaultMessage());
            return item;
        }).collect(Collectors.toList()));
        logger.error("Validation error", ex);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * This method handles DataIntegrityViolationException exception. Handles Unique key constraints like LOB, Demand_Id, RR_Number
     * Created By : pratish.b
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleUniqueConstraintViolation(DataIntegrityViolationException ex) {

        org.hibernate.exception.ConstraintViolationException cve = findConstraintViolation(ex);

        if (cve != null) {
            String constraintName = cve.getConstraintName();
            logger.info("****___________ Inside handleUniqueConstraintViolation, constraintName is {} ", cve.getConstraintName());
            if ("ADD_NEW_DEMANDS.DEMAND_ID".equalsIgnoreCase(constraintName)) {
                String message = "Duplicate entry violates unique constraint: " + constraintName + ". Demand ID already exists.";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
            } else if ("ADD_NEW_DEMANDS.RR_NUMBER".equalsIgnoreCase(constraintName)) {
                String message = "Duplicate entry violates unique constraint:  " + cve.getCause().getLocalizedMessage() + ". RR Number already exists.";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
            } else if ("DUMMY.LOB".equalsIgnoreCase(constraintName)) {
                String message = "Duplicate entry violates unique constraint: LOB already exists.";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
            }
        }
        // Generic fallback
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Data integrity violation occurred.");
    }

    /**
     * Created By : pratish.b
     */
    private org.hibernate.exception.ConstraintViolationException findConstraintViolation(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                return cve;
            }
            cause = cause.getCause();
        }
        return null;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(ex.getParameterName() + " is required");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(msg);
    }

    @ExceptionHandler(DMAGenericRuntimeException.class)
    public ResponseEntity<?> handleDMAGenericException(DMAGenericRuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
