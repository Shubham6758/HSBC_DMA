package com.coforge.hsbcdma.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) that holds user registration details.
 *
 * @author Vandana Pal
 */

@Data
public class RegisterRequestDTO {

    private String userId;
    private String password;

    private Long roleId;
    private Long countryId;

    private String name;
    private String tempPwd;
    private Long phoneNumber;
    private String emailId;

    private Long locationId;
    private Long departmentId;
    private Long subdepartmentId;
}
