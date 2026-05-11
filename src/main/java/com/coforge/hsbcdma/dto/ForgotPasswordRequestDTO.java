package com.coforge.hsbcdma.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used for forgot password requests.
 * It contains the user ID and registered email address.
 *
 * @author Vandana Pal
 */

@Data
public class ForgotPasswordRequestDTO {
    @NotBlank
    private String userId;

    @NotBlank
    @Email
    private String emailId;
}
