package com.coforge.hsbcdma.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created By : pratish.b
 */

@Getter
@Setter
public class ApiResponse<T> {

    private static final Logger logger = LoggerFactory.getLogger(ApiResponse.class);

    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data){
       return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data){
        return new ApiResponse<>(true,message,data);
    }

    public static <T> ApiResponse<T> error(String message){
       return new ApiResponse<>(false, message, null);
    }
}
