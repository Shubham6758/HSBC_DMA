package com.coforge.hsbcdma.service.UserManagementServices;

import com.coforge.hsbcdma.dto.RolemgmtDTO.*;
import com.coforge.hsbcdma.entity.Roles;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.entity.UserManagementEntities.Action;
import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModule;
import com.coforge.hsbcdma.entity.UserManagementEntities.Module;
import com.coforge.hsbcdma.mapper.RoleMapper;
import com.coforge.hsbcdma.repository.RolesRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ActionRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ChildModuleRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ModuleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.coforge.hsbcdma.mapper.RoleMapper.toGetAllResponse;

@Service
public class RoleService {

    @Autowired
    private RolesRepository roleRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ChildModuleRepository childModuleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ActionRepository actionRepository;

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Transactional
    public RoleCreateResponse createRole(RoleCreateRequest request) {

        if (roleRepository.existsByRoleIgnoreCase(request.getRole())) {
            throw new RuntimeException("Role already exists: " + request.getRole());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated())
                ? auth.getName()
                : "SYSTEM";
        logger.info("*****username*****{}",username);
        Optional<User> user = Optional.ofNullable(userAccountRepository.findByUserId(username).orElseThrow(() -> new RuntimeException("User Not Found")));

        Roles role = new Roles();
        role.setRole(request.getRole().trim());
//        role.setCreatedBy(username+"-"+user.get().getName());
        role.setCreatedAt(LocalDateTime.now());
        role.setCreatedBy(user.get());
        role.setActive(true);
        role.setModuleChildModule(request.getModuleChildModule());
        Roles saved = roleRepository.save(role);
        return toResponse(saved);
    }


    @Transactional
    public List<GetAllModuleChildModuleResponse> getAllModuleChildModuleResponse() {

        List<ChildModule> entities = childModuleRepository.findAll();

        logger.info("****childModule size******* {}", entities.size());

        // Group by moduleId
        Map<Long, List<ChildModule>> grouped =
                entities.stream()
                        .collect(Collectors.groupingBy(cm -> cm.getModule().getId()));

        logger.info("********grouped keys********* {}", grouped.keySet());

        return grouped.values().stream()
                .map(cms -> {
                    ChildModule first = cms.get(0);

                    List<GetAllModuleChildModuleResponse.ChildModuleItem> items =
                            cms.stream()
                                    .map(cm -> {
                                        // map Action entities -> ActionDTO
                                        List<GetAllModuleChildModuleResponse.ChildModuleItem.ActionDTO> actions =
                                                (cm.getActions() == null ? List.<GetAllModuleChildModuleResponse.ChildModuleItem.ActionDTO>of()
                                                        : cm.getActions().stream()
                                                        .map(a -> new GetAllModuleChildModuleResponse.ChildModuleItem.ActionDTO(
                                                                a.getId(),
                                                                a.getAction()     // your Action entity field name
                                                        ))
                                                        .toList()
                                                );

                                        return new GetAllModuleChildModuleResponse.ChildModuleItem(
                                                cm.getId(),
                                                cm.getChildModule(),   // child module name getter
                                                actions
                                        );
                                    })
                                    .toList();

                    return new GetAllModuleChildModuleResponse(
                            first.getModule().getId(),
                            first.getModule().getModule(),
                            items
                    );
                })
                .toList();
    }


    public List<RoleResponse> getAllRoles() {

        List<Roles> roles = roleRepository.findAll();
        Set<Long> moduleIds = new HashSet<>();
        Set<Long> childIds  = new HashSet<>();
        Set<Long> actionIds = new HashSet<>();
        logger.info("****roles****{}",roles.stream().toList());
        Map<Long, Set<Long>> actionIdsByChildId = new HashMap<>();
        for (Roles r : roles) {
            List<ModuleChildModuleDTO> list = r.getModuleChildModule();
            if (list == null) continue;

            for (ModuleChildModuleDTO dto : list) {
                if (dto == null) continue;

                if (dto.getModuleId() != null) {
                    moduleIds.add(dto.getModuleId());
                }

                if (dto.getChildModules() != null) {
                    for (ModuleChildModuleDTO.ChildSelectionDTO sel : dto.getChildModules()) {
                        if (sel == null || sel.getChildModuleId() == null) continue;

                        Long childId = sel.getChildModuleId();
                        if (childId == null) continue;

                        childIds.add(childId);

                        List<Long> selectedActionIds = sel.getActionIds(); // List<Long>
                        if (selectedActionIds != null && !selectedActionIds.isEmpty()) {

                            actionIds.addAll(selectedActionIds);

                            actionIdsByChildId
                                    .computeIfAbsent(childId, k -> new HashSet<>())
                                    .addAll(selectedActionIds);
                        }

                    }
                }
            }
        }

        logger.info("*********actionIds (from JSON)*********{}", actionIdsByChildId);

        Map<Long, String> moduleNameById = moduleRepository.findAllById(moduleIds).stream()
                .collect(Collectors.toMap(Module::getId, Module::getModule));
        logger.info("*******modulenamebyid*************{}", Arrays.toString(moduleNameById.values().toArray()));

        Map<Long, String> childNameById = childModuleRepository.findAllById(childIds).stream()
                .collect(Collectors.toMap(ChildModule::getId, ChildModule::getChildModule));
        logger.info("*********childmodule name*********{}", childNameById.values());


        Map<Long, String> actionNameById = actionRepository.findAllById(actionIds).stream()
                .collect(Collectors.toMap(Action::getId, Action::getAction));


        // IMPORTANT: remove the DB-based action map; we already built it from JSON
        // Map<Long, Boolean> actionFlagByChildId = childModuleRepository.findAllById(childIds) ...
        logger.info("******role*****{}",roles.toString());
        // Now map each role using mapper (no DB calls inside mapper)
        return roles.stream()
                .map(role -> toGetAllResponse(role, moduleNameById, childNameById, actionNameById))
                .toList();
    }

    @Transactional
    public RoleResponse updateRole(Long roleId, RoleCreateRequest request) {

        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.getName() != null) ? auth.getName() : "SYSTEM";

        logger.info("*****username*****{}",username);
        Optional<User> user = Optional.ofNullable(userAccountRepository.findByUserId(username).orElseThrow(() -> new RuntimeException("User Not Found")));

        String newRoleName = request.getRole();
        if (newRoleName == null || newRoleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null/empty");
        }

        logger.info("Updating roleId={} by user={}, requestRole={}", roleId, username, newRoleName);

        role.setRole(newRoleName.trim());
        role.setUpdateAt(LocalDateTime.now());
//        role.setUpdatedBy(username+"-"+user.get().getName());

        role.setUpdatedBy(user.get());
        List<ModuleChildModuleDTO> childModules =
                Optional.ofNullable(request.getModuleChildModule()).orElse(Collections.emptyList());

        logger.info("Incoming moduleChildModule size={}", childModules.size());

        // Collect ids for bulk validation/fetch (null-safe)
        Set<Long> moduleIds = childModules.stream()
                .filter(Objects::nonNull)
                .map(ModuleChildModuleDTO::getModuleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> childIds = childModules.stream()
                .filter(Objects::nonNull)
                .flatMap(dto -> Optional.ofNullable(dto.getChildModules()).orElse(Collections.emptyList()).stream())
                .filter(Objects::nonNull)
                .map(ModuleChildModuleDTO.ChildSelectionDTO::getChildModuleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        Set<Long> actionIds = childModules.stream()
                .filter(Objects::nonNull)
                .flatMap(dto -> Optional.ofNullable(dto.getChildModules()).orElse(Collections.emptyList()).stream())
                .filter(Objects::nonNull)
                .flatMap(sel -> Optional.ofNullable(sel.getActionIds()).orElse(Collections.emptyList()).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        logger.info("Collected moduleIds={}, childIds={}, actionIds={}", moduleIds, childIds, actionIds);


        // Load entities in bulk
        Map<Long, Module> modules = moduleRepository.findAllById(moduleIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Module::getId, m -> m));

        Map<Long, ChildModule> children = childModuleRepository.findAllById(childIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ChildModule::getId, c -> c));

        Map<Long, Action> actions = actionRepository.findAllById(actionIds).stream()
                .collect(Collectors.toMap(Action::getId, a -> a));

        logger.debug("Fetched modulesCount={}, childrenCount={}", modules.size(), children.size());

        // Identify missing IDs (useful in logs)

        Set<Long> missingModules = new HashSet<>(moduleIds);
        missingModules.removeAll(modules.keySet());

        Set<Long> missingChildren = new HashSet<>(childIds);
        missingChildren.removeAll(children.keySet());

        if (!missingModules.isEmpty() || !missingChildren.isEmpty()) {
            logger.warn("Missing in DB => modules={}, childModules={}", missingModules, missingChildren);
        }

        // Validate existence + parent-child relationship
        List<String> errors = new ArrayList<>();

        for (ModuleChildModuleDTO dto : childModules) {
            if (dto == null) {
                errors.add("ModuleChildModuleDTO entry is null");
                continue;
            }

            Long modId = dto.getModuleId();
            if (modId == null || !modules.containsKey(modId)) {
                errors.add("Invalid module id: " + modId);
                continue; // can't validate children relationship without module
            }

            List<ModuleChildModuleDTO.ChildSelectionDTO> selections =
                    Optional.ofNullable(dto.getChildModules()).orElse(Collections.emptyList());

            for (ModuleChildModuleDTO.ChildSelectionDTO sel : selections) {
                if (sel == null || sel.getChildModuleId() == null) {
                    errors.add("Invalid childModule id: null (module " + modId + ")");
                    continue;
                }

                Long cid = sel.getChildModuleId();
                ChildModule ch = children.get(cid);

                if (ch == null) {
                    errors.add("Invalid childModule id: " + cid);
                } else if (ch.getModule() == null || !Objects.equals(ch.getModule().getId(), modId)) {
                    errors.add("ChildModule " + cid + " does not belong to Module " + modId);
                }
            }
        }

        if (!errors.isEmpty()) {
            logger.error("Validation failed for roleId={}, errors={}", roleId, errors);
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        // Normalize: if action map is null, set empty map to keep JSON consistent
        childModules.forEach(m ->
                Optional.ofNullable(m.getChildModules()).orElse(Collections.emptyList())
                        .forEach(c -> {
                            if (c != null && c.getActionIds() == null) {
                                c.setActionIds(Collections.emptyList());
                            }
                        })
        );

        // Persist the new state for the role (PUT = replace)
        role.setModuleChildModule(childModules);
        Roles saved = roleRepository.save(role);

        logger.info("Role updated successfully roleId={}, savedRoleName={}", saved.getId(), saved.getRole());

        // Build response with names
        Map<Long, String> moduleNameById = modules.values().stream()
                .collect(Collectors.toMap(Module::getId, Module::getModule));

        Map<Long, String> childNameById = children.values().stream()
                .collect(Collectors.toMap(ChildModule::getId, ChildModule::getChildModule));


        Map<Long, String> actionNameById = actions.values().stream()
                .collect(Collectors.toMap(Action::getId, Action::getAction));

        // Return response
        // If your mapper accepts actionFlagByChildId, pass it:
        // return RoleMapper.toRoleResponse(saved, moduleNameById, childNameById, actionFlagByChildId);

        // If mapper needs full map:
        // return RoleMapper.toRoleResponse(saved, moduleNameById, childNameById, actionMapByChildId);

        // If mapper not updated yet:
        return RoleMapper.toRoleResponse(saved, moduleNameById, childNameById,actionNameById);
    }

    public void updateStatus(Long userId,boolean active){
        Roles role = roleRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Role not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.getName() != null) ? auth.getName() : "SYSTEM";
        logger.info("*****username*****{}",username);
        Optional<User> user = Optional.ofNullable(userAccountRepository.findByUserId(username).orElseThrow(() -> new RuntimeException("User Not Found")));
        role.setUpdatedBy(user.get());
        role.setActive(active);
        role.setUpdateAt(LocalDateTime.now());
        roleRepository.save(role);

    }

    private RoleCreateResponse toResponse(Roles r) {
        return RoleCreateResponse.builder()
                .id(r.getId())
                .role(r.getRole())
                .createdBy(r.getCreatedBy().getUserId())
                .createdAt(r.getCreatedAt())
                .moduleChildModule(r.getModuleChildModule())
                .build();
    }

}