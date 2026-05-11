package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.RolesPermissionDTO;
import com.coforge.hsbcdma.repository.PermissionsRepository;
import com.coforge.hsbcdma.repository.RolesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created By : pratish.b
 */
@Service
public class RolePermissionService {

    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private PermissionsRepository permissionRepository;

    private static final Logger logger = LoggerFactory.getLogger(RolePermissionService.class);

    public RolesPermissionDTO getRolesAndPermissions(){

        RolesPermissionDTO rolesPermissionDTO = new RolesPermissionDTO();

        rolesPermissionDTO.setRolesList(rolesRepository.findAll());
        rolesPermissionDTO.setPermissionsList(permissionRepository.findAll());

        return rolesPermissionDTO;
    }
}
