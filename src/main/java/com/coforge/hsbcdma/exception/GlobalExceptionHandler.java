package com.coforge.hsbcdma.exception;

import com.coforge.hsbcdma.domain.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
/**
 * This is a aspect class for handling the exceptions globally.
 * Created By : pratish.b
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);




    /**
     * This method handles generic exception.
     * Created By : pratish.b
     */
   @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, WebRequest request) {
        logger.error("*** Inside handleGeneric() method.Unhandled exception", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred. Please try again later.");
        //problemDetail.setType(URI.create("http://localhost:8080/problems/internal-error"));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        return problemDetail;
    }

    /**
     * This method handles generic runtime exception. Catch-all for unexpected runtime errors.
     * Created By : pratish.b
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        logger.error("Unhandled runtime exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError body = new ApiError(
                "Internal Server Error",
                // Avoid leaking internal details to clients in production
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * This method is fallback to  any other Throwable. Catch-all for unexpected runtime errors.
     * Created By : pratish.b
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(Throwable ex, HttpServletRequest request) {
        logger.error("Unhandled error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiError body = new ApiError(
                "Internal Server Error",
                "Unexpected error occurred.",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getParameterName() + " is required");
    }
}
