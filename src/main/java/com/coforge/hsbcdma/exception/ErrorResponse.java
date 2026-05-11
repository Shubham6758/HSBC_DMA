package com.coforge.hsbcdma.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
  * Created By : pratish.b
 */
@Getter
@Setter
public class ErrorResponse {

        private String code;           // business error code, e.g. DEMAND_ID_NOT_FOUND
        private String message;        // human-readable message
        private int status;            // HTTP status code
        private String path;           // request path
        private OffsetDateTime timestamp;
        private List<FieldErrorItem> errors; // for validation details

        @Getter
        @Setter
        public static class FieldErrorItem {
            private String field;
            private String error;
        }
    }
