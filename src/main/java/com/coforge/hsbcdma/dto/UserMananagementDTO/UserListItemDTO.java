package com.coforge.hsbcdma.dto.UserMananagementDTO;


import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.CountryRefDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.NamedRefDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.RoleRefDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// Created by Chetan
@Data
@AllArgsConstructor
public class UserListItemDTO {
    private Long id;
    private String userId;
    private String name;
    private String emailId;
    private Long phoneNumber;
    private boolean active;
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String createdByName;
    private String updatedBy;
    private String updatedByName;

    private RoleRefDTO role;          // id + role
    private CountryRefDTO country;     // id + callingCode
    private NamedRefDTO location;      // id + name
    private NamedRefDTO department;    // id + name
    private NamedRefDTO subdepartment; // id + name

}

