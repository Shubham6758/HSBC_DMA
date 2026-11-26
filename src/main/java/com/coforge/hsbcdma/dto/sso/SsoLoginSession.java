package com.coforge.hsbcdma.dto.sso;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsoLoginSession {

    private AuthResponseDTO authResponse;

    private LocalDateTime expiresAt;

    private boolean used;
}