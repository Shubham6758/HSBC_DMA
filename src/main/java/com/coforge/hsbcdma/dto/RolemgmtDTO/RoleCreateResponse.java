package com.coforge.hsbcdma.dto.RolemgmtDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Data
public class RoleCreateResponse {
    Long id;
     String role;
     String createdBy;
     LocalDateTime createdAt;
     List<ModuleChildModuleDTO> moduleChildModule;
}
