package com.coforge.hsbcdma.rbac;

import com.coforge.hsbcdma.dto.RolemgmtDTO.ModuleChildModuleDTO;
import com.coforge.hsbcdma.entity.Roles;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DynamicPermissionService {

    private final UserAccountRepository userRepository;

    public boolean userHasAction(
            String userId,
            Long requiredActionId
    ) {
        if (userId == null
                || userId.isBlank()
                || requiredActionId == null) {
            return false;
        }

        User user = userRepository
                .findActiveUserWithActiveRole(userId)
                .orElse(null);

        if (user == null || user.getRole() == null) {
            return false;
        }

        Roles role = user.getRole();

        if (!role.isActive()) {
            return false;
        }

        List<ModuleChildModuleDTO> modulePermissions =
                role.getModuleChildModule();

        if (modulePermissions == null
                || modulePermissions.isEmpty()) {
            return false;
        }

        return modulePermissions.stream()
                .filter(Objects::nonNull)

                .map(ModuleChildModuleDTO::getChildModules)
                .filter(Objects::nonNull)

                .flatMap(Collection::stream)
                .filter(Objects::nonNull)

                .map(
                        ModuleChildModuleDTO.ChildSelectionDTO
                                ::getActionIds
                )
                .filter(Objects::nonNull)

                .flatMap(Collection::stream)
                .filter(Objects::nonNull)

                .anyMatch(requiredActionId::equals);
    }
}