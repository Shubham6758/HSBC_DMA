package com.coforge.hsbcdma.dto.RolemgmtDTO;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// Get all roles DTOs
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
        private Long id;
        private String role;
//        private String createdBy;
        private UserSummary createBy;
        private LocalDateTime createdAt;
        private UserSummary updatedBy;
        private LocalDateTime updatedAt;
        private boolean isactive;

        private List<ModuleGroupResponse> moduleChildModule;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ModuleGroupResponse {
            private Long moduleId;
            private String moduleName;
            private List<ChildModuleResponse> childModules;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ChildModuleResponse {
            private Long childModuleId;
            private String childModuleName;
            List<ChildModuleActionResponse>actions;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ChildModuleActionResponse{
            private Long actionId;
            private String actionName;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserSummary{
            private String name;
            private String userId;
        }
}

