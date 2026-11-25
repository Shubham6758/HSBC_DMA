package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecryptEmailResponse {

    private String decryptedEmail;
    private String message;

}
