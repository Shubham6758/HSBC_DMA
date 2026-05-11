package com.coforge.hsbcdma.dto.RolemgmtDTO;

import lombok.*;

import java.util.List;
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetRoleResponse {
    String role;
    private List<RoleResponse.ModuleGroupResponse> moduleChildModule;
}
