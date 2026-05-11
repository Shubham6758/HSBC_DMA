package com.coforge.hsbcdma.dto.RolemgmtDTO;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllModuleChildModuleResponse {
    private Long moduleId;
    private String moduleName;
    private List<ChildModuleItem> childModules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildModuleItem {
        private Long childModuleId;
        private String childModuleName;
//        private Boolean action;
        private List<ActionDTO>actions;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ActionDTO{
            private Long actionId;
            private String actionName;
        }
    }
}
