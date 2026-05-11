package com.coforge.hsbcdma.dto.RolemgmtDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleChildModuleDTO {
    private Long moduleId;
//    private List<Long> childModule;
    private List<ChildSelectionDTO> childModules;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildSelectionDTO {
        private Long childModuleId;
//        private Boolean action;
//        Map<String,Boolean>action;
        List<Long>actionIds;
    }
}
