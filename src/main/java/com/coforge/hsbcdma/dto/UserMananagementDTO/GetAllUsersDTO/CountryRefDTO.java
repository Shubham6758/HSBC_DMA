package com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryRefDTO {
    private Long id;
    private String name;
    private String callingCode;
}

