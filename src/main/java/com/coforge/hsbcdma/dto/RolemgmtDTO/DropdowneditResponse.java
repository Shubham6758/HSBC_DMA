package com.coforge.hsbcdma.dto.RolemgmtDTO;

import com.coforge.hsbcdma.dto.UserMananagementDTO.DropdownResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class DropdowneditResponse {
    List<DropdownResponse.RoleResponseDTO> rolesList;
    List<GetAllModuleChildModuleResponse>getAllModuleChildModuleResponseList;
}
