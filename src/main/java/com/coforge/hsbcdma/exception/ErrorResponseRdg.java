package com.coforge.hsbcdma.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseRdg {

    public  String code;
    public  String message;
    public LocalDateTime timestamp;
    public ErrorResponseRdg(String message,String code,LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp=timestamp;
    }


}
