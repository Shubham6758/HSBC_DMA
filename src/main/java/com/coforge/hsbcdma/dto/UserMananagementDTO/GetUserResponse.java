package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {
    String userId;
    String name;
    String emailid;
    String role;
    Long phoneNumber;
}
