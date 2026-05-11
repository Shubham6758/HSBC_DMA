package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {

    String moduleName;
    List<String> moduleActions;
    List<ChildModuleDTO> childModuleDTO;

}
