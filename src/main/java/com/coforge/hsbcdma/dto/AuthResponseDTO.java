package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.dto.RolemgmtDTO.GetRoleResponse;
import com.coforge.hsbcdma.dto.RolemgmtDTO.ModuleChildModuleDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String userId;
    private String token;          // access token
    private String refreshToken;   // ✅ refresh token
    private GetRoleResponse role;
    private String status;
    private String name;

    // Used for PASSWORD_CHANGE_REQUIRED
    public AuthResponseDTO(String status) {
        this.status = status;
    }

    // ✅ FINAL login‑success constructor (USE THIS)
    public AuthResponseDTO(
            @NotBlank(message = "UserId can not be blank") String userId,
            String token,
            String refreshToken,
            GetRoleResponse role,
            String name
    ) {
        this.userId = userId;
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
        this.status = "SUCCESS";
        this.name = name;
    }

    // (optional / unused legacy constructor — can be removed later)
    public AuthResponseDTO(
            String userId,
            String token,
            GetRoleResponse role,
            String name
    ) {
        this.userId = userId;
        this.token = token;
        this.role = role;
        this.status = "SUCCESS";
        this.name = name;
    }

    public record RolesDTO(
            List<ModuleChildModuleDTO> moduleChildModuleDTO,
            String name
    ){}
}
