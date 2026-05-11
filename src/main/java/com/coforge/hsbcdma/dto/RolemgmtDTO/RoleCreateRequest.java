package com.coforge.hsbcdma.dto.RolemgmtDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleCreateRequest {

    @NotBlank(message = "role is required")
    private String role;
//    private Map<String, Object> moduleActions;
    private List<ModuleChildModuleDTO> moduleChildModule;
}

