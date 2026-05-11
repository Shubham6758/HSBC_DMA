package com.coforge.hsbcdma.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) that holds user details for login response.
 *
 * @author Vandana Pal
 */

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
//    private Long id;
    private String userId;
    private String tempPassword;

}
