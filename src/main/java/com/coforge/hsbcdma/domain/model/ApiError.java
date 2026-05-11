package com.coforge.hsbcdma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

/**
 * Created By : pratish.b
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String error;
    private String message;
    private String path;
    private int status;
    private Instant timestamp;
}
