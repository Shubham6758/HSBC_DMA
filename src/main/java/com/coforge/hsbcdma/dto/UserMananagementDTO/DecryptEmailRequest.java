package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.Data;

@Data
public class DecryptEmailRequest {

    private String encryptedEmail;
    private String password;

}
