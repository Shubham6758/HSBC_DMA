package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.entity.Permissions;
import com.coforge.hsbcdma.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolesPermissionDTO {

    List<Roles> rolesList;
    List<Permissions> permissionsList;
}
