package com.coforge.hsbcdma.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) that holds user login details.
 *
 * @author Vandana Pal
 */

@Data
public class LoginRequestDTO {
    private String userId;
    private String password;
    private String tempPassword;
}
