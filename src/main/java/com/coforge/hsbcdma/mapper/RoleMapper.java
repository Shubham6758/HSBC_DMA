package com.coforge.hsbcdma.mapper;

import com.coforge.hsbcdma.dto.RolemgmtDTO.GetAllModuleChildModuleResponse;
import com.coforge.hsbcdma.dto.RolemgmtDTO.GetRoleResponse;
import com.coforge.hsbcdma.dto.RolemgmtDTO.ModuleChildModuleDTO;
import com.coforge.hsbcdma.dto.RolemgmtDTO.RoleResponse;
import com.coforge.hsbcdma.entity.Roles;
import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoleMapper {

    public static GetAllModuleChildModuleResponse toGroupedDto(
            ChildModule first,
            List<GetAllModuleChildModuleResponse.ChildModuleItem> items
    ) {
        return new GetAllModuleChildModuleResponse(
                first.getModule().getId(),
                first.getModule().getModule(),
                items
        );
    }

    public static RoleResponse toRoleResponse(
            Roles role,
            Map<Long, String> moduleNameById,
            Map<Long, String> childNameById,
            Map<Long, String> actionNameById
    ) {
        if (role == null) return null;

        List<ModuleChildModuleDTO> jsonList =
                role.getModuleChildModule() == null ? Collections.emptyList() : role.getModuleChildModule();

        List<RoleResponse.ModuleGroupResponse> grouped = jsonList.stream()
                .filter(Objects::nonNull)
                .map(entry -> {

                    Long moduleId = entry.getModuleId();
                    String moduleName = moduleNameById.getOrDefault(moduleId, "UNKNOWN");

                    List<ModuleChildModuleDTO.ChildSelectionDTO> childSelections =
                            entry.getChildModules() == null ? Collections.emptyList() : entry.getChildModules();

                    List<RoleResponse.ChildModuleResponse> children = childSelections.stream()
                            .filter(Objects::nonNull)
                            .map(sel -> {

                                Long childId = sel.getChildModuleId();
                                String childName = childNameById.getOrDefault(childId, "UNKNOWN");


                                List<Long> actionIds =
                                        sel.getActionIds() == null ? Collections.emptyList() : sel.getActionIds();


                                List<RoleResponse.ChildModuleActionResponse> actions = actionIds.stream()
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .map(aid -> new RoleResponse.ChildModuleActionResponse(
                                                aid,
                                                actionNameById.getOrDefault(aid, "UNKNOWN")
                                        ))
                                        .toList();

                                return new RoleResponse.ChildModuleResponse(childId, childName, actions);
                            })
                            .toList();

                    return new RoleResponse.ModuleGroupResponse(moduleId, moduleName, children);
                })
                .toList();

        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRole(role.getRole());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdateAt());
        response.setUpdatedBy(new RoleResponse.UserSummary(role.getUpdatedBy().getUserId(),role.getUpdatedBy().getName()));
        response.setModuleChildModule(grouped);
        response.setCreateBy(new RoleResponse.UserSummary(role.getCreatedBy().getName(), role.getCreatedBy().getUserId()));

        return response;
    }




    public static RoleResponse toGetAllResponse(
            Roles role,
            Map<Long, String> moduleNameById,
            Map<Long, String> childNameById,
            Map<Long, String> actionNameById
    ) {
        if (role == null) return null;

        List<ModuleChildModuleDTO> jsonList =
                role.getModuleChildModule() == null ? Collections.emptyList() : role.getModuleChildModule();

        List<RoleResponse.ModuleGroupResponse> grouped = jsonList.stream()
                .filter(Objects::nonNull)
                .map(entry -> {
                    Long moduleId = entry.getModuleId();
                    String moduleName = moduleNameById.getOrDefault(moduleId, "UNKNOWN");

                    List<ModuleChildModuleDTO.ChildSelectionDTO> childSelections =
                            entry.getChildModules() == null ? Collections.emptyList() : entry.getChildModules();

                    List<RoleResponse.ChildModuleResponse> children = childSelections.stream()
                            .filter(Objects::nonNull)
                            .map(selectionDTO -> {
                                Long childId = selectionDTO.getChildModuleId();
                                String childName = childNameById.getOrDefault(childId, "UNKNOWN");

                                List<Long> actionIds =
                                        selectionDTO.getActionIds() == null ? Collections.emptyList() : selectionDTO.getActionIds();

                                // Convert actionIds -> List<ChildModuleActionResponse>
                                List<RoleResponse.ChildModuleActionResponse> actions = actionIds.stream()
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .map(aid -> new RoleResponse.ChildModuleActionResponse(
                                                aid,
                                                actionNameById.getOrDefault(aid, "UNKNOWN")
                                        ))
                                        .toList();

                                return new RoleResponse.ChildModuleResponse(childId, childName, actions);
                            })
                            .toList();

                    return new RoleResponse.ModuleGroupResponse(moduleId, moduleName, children);
                })
                .toList();

        // Build final response
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRole(role.getRole());
        response.setCreatedAt(role.getCreatedAt());
//        response.setUpdatedBy(new RoleResponse.UserSummary(role.getUpdatedBy().getUserId(),role.getUpdatedBy().getName()));
        if (role.getUpdatedBy() != null) {
            String uName = safeName(role.getUpdatedBy().getName());
            String uUserId = safeId(role.getUpdatedBy().getUserId());
            response.setUpdatedBy(new RoleResponse.UserSummary(uName, uUserId));
        } else {
            response.setUpdatedBy(null);
        }
        response.setUpdatedAt(role.getUpdateAt());
        response.setIsactive(role.isActive());
        response.setModuleChildModule(grouped);
//        response.setCreateBy(new RoleResponse.UserSummary(role.getCreatedBy().getName(), role.getCreatedBy().getUserId()));
        if (role.getCreatedBy() != null) {
            String cName = safeName(role.getCreatedBy().getName());
            String cUserId = safeId(role.getCreatedBy().getUserId());
            response.setCreateBy(new RoleResponse.UserSummary(cName, cUserId));
        } else {
            response.setCreateBy(null);
        }
        return response;
    }

    public static GetRoleResponse toGetRoleResponse(
            Roles role,
            Map<Long, String> moduleIdToName,
            Map<Long, String> childIdToName,
            Map<Long, String> actionIdToName
    ) {
        if (role == null) return null;

        List<ModuleChildModuleDTO> moduleList =
                role.getModuleChildModule() == null ? Collections.emptyList() : role.getModuleChildModule();

        List<RoleResponse.ModuleGroupResponse> modules = moduleList.stream()
                .filter(Objects::nonNull)
                .map(mg -> {
                    Long moduleId = mg.getModuleId();

                    List<ModuleChildModuleDTO.ChildSelectionDTO> childList =
                            mg.getChildModules() == null ? Collections.emptyList() : mg.getChildModules();

                    List<RoleResponse.ChildModuleResponse> children = childList.stream()
                            .filter(Objects::nonNull)
                            .map(cm -> {
                                Long childId = cm.getChildModuleId();

                                List<Long> actionIds =
                                        cm.getActionIds() == null ? Collections.emptyList() : cm.getActionIds();


                                List<RoleResponse.ChildModuleActionResponse> actions = actionIds.stream()
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .map(aid -> new RoleResponse.ChildModuleActionResponse(
                                                aid,
                                                actionIdToName.getOrDefault(aid, "UNKNOWN")
                                        ))
                                        .toList();



                                return new RoleResponse.ChildModuleResponse(
                                        childId,
                                        childIdToName.getOrDefault(childId, "UNKNOWN"),
                                        actions
                                );
                            })
                            .toList();

                    return new RoleResponse.ModuleGroupResponse(
                            moduleId,
                            moduleIdToName.getOrDefault(moduleId, "UNKNOWN"),
                            children
                    );
                })
                .toList();

        return new GetRoleResponse(role.getRole(), modules);
    }

    /** Helpers to avoid NPE and ensure String userId. */
    private static String safeId(Object id) {
        return id == null ? null : String.valueOf(id);
    }
    private static String safeName(String name) {
        return name == null ? null : name;
    }
}
