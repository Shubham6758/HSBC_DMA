package com.coforge.hsbcdma.service.Sso;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import com.coforge.hsbcdma.dto.RolemgmtDTO.GetRoleResponse;
import com.coforge.hsbcdma.entity.Roles;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.entity.UserManagementEntities.Action;
import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModule;
import com.coforge.hsbcdma.entity.UserManagementEntities.Module;
import com.coforge.hsbcdma.exception.InvalidCredentialsException;
import com.coforge.hsbcdma.exception.UnauthorizedException;
import com.coforge.hsbcdma.exception.UserInactiveException;
import com.coforge.hsbcdma.mapper.RoleMapper;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ActionRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ChildModuleRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ModuleRepository;
import com.coforge.hsbcdma.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SsoService {
    private final UserAccountRepository userAccountRepository;
    private final JwtUtil jwtUtil;
    private final ActionRepository actionRepository;
    private final ModuleRepository moduleRepository;
    private final ChildModuleRepository childModuleRepository;


    public AuthResponseDTO processSSOUser(String email){
        User user = userAccountRepository.findByEmailId(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if(!user.isActive()){
            throw new UserInactiveException("User is inactive");
        }

        if (user.getAccountLocked()) {
            throw new InvalidCredentialsException(
                    "Account is locked");
        }

        if (user.getActiveToken() != null &&
            user.getTokenExpiresAt() != null &&
            user.getTokenExpiresAt().isAfter(LocalDateTime.now())) {

            throw new InvalidCredentialsException(
                    "User already logged in");
        }
        String accessToken =
            jwtUtil.generateToken(
                    user.getUserId(),
                    user.getRole().getRole(),
                    user.getRole().getModuleChildModule()
            );


        String refreshToken =
            jwtUtil.generateRefreshToken(
                    user.getUserId()

            );

        user.setActiveToken(accessToken);
        user.setTokenExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );
        userAccountRepository.save(user);
        GetRoleResponse roleInfo = buildRoleResponse(user.getRole());
        return new AuthResponseDTO(
                user.getUserId(),
                accessToken,
                refreshToken,
                roleInfo,
                user.getName()
        );
    }

    private GetRoleResponse buildRoleResponse(Roles role) {

        List<Long> moduleIds = role.getModuleChildModule()
                .stream()
                .map(mg -> mg.getModuleId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Long> childIds = role.getModuleChildModule()
                .stream()
                .flatMap(mg -> mg.getChildModules().stream())
                .map(cm -> cm.getChildModuleId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> moduleIdToName =
                moduleRepository.findAllById(moduleIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Module::getId,
                                Module::getModule,
                                (a, b) -> a
                        ));

        Map<Long, String> childIdToName =
                childModuleRepository.findAllById(childIds)
                        .stream()
                        .collect(Collectors.toMap(
                                ChildModule::getId,
                                ChildModule::getChildModule,
                                (a, b) -> a
                        ));

        List<Long> actionIds = role.getModuleChildModule().stream()
                .filter(Objects::nonNull)
                .flatMap(mg -> mg.getChildModules() == null
                        ? Stream.empty()
                        : mg.getChildModules().stream())
                .filter(Objects::nonNull)
                .flatMap(cm -> cm.getActionIds() == null
                        ? Stream.empty()
                        : cm.getActionIds().stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> actionIdToName =
                actionRepository.findAllById(actionIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Action::getId,
                                Action::getAction,
                                (a, b) -> a
                        ));

        return RoleMapper.toGetRoleResponse(
                role,
                moduleIdToName,
                childIdToName,
                actionIdToName
        );
    }

}
