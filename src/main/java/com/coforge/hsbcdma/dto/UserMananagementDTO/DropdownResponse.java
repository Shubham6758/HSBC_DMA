package com.coforge.hsbcdma.dto.UserMananagementDTO;

import com.coforge.hsbcdma.entity.SubDepartment;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DropdownResponse {

    private List<?> countryCodes;
    private List<RoleResponseDTO> roles;
    private List<?> locations;
    private List<?> departments;
    private Map<String, List<SubDepartment>> subDepartments;
    @Data
    public static class RoleResponseDTO{
        Long id;
        String role;

        public RoleResponseDTO(Long id, String role) {
            this.id = id;
            this.role = role;
        }
    }
}
