package com.coforge.hsbcdma.dto.UserMananagementDTO;


import lombok.Data;

// Created by Chetan
@Data
public class PutUserRequestDTO {
    private String userId;
    private String name;
    private String emailId;
    private Long phoneNumber;

    private Long roleId;
    private Long countryId;

    private Long locationId;
    private Long departmentId;
    private Long subdepartmentId;

    private Boolean active;
}
