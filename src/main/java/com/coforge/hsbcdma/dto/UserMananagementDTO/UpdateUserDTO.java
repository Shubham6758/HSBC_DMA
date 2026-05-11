package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Data
public class UpdateUserDTO {
    String userId;
    String name;
    String emailId;
    String role;
    String phoneNumber;
    String countryCode;
}
