package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.RolemgmtDTO.RoleCreateRequest;
import com.coforge.hsbcdma.dto.RolemgmtDTO.RoleCreateResponse;
import com.coforge.hsbcdma.dto.RolemgmtDTO.RoleResponse;
import com.coforge.hsbcdma.dto.UserMananagementDTO.PutUserRequestDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.UserListItemDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.UserStatusUpdateRequestDTO;
import com.coforge.hsbcdma.service.UserAccountService;
import com.coforge.hsbcdma.service.UserManagementServices.DropdownService;
import com.coforge.hsbcdma.service.UserManagementServices.RoleService;
import com.coforge.hsbcdma.service.UserManagementServices.UserManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_management")
public class UserManagementController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RoleService roleService;

    @Autowired
    UserAccountService userService;

    @Autowired
    private DropdownService dropdownService;


//    @PostMapping("/create_module_action")
//    public ResponseEntity<?> create(@RequestBody List<ModuleDTO> moduleDTOList) throws Exception{
//        userManagementService.createModuleAndAction(moduleDTOList);
//        return success("Module created successfully.");
//    }

//    @PutMapping("/update")
//    public ResponseEntity<?>updateUser(@RequestBody UpdateUserDTO updateUserDTO){
//        UpdateUserDTO updated = userService.updateUser(updateUserDTO);
//        return success("Update Successfully");
//    }

    @PostMapping("/createrole")
    ResponseEntity<?>createRole(@RequestBody RoleCreateRequest roleCreateRequest){
        RoleCreateResponse roleCreateResponse = roleService.createRole(roleCreateRequest);
        return create(roleCreateResponse);
    }


    @GetMapping("/getallmodulechildmodules")
    public ResponseEntity<?> getAllModuleChildModuleResponse() {
        return success(roleService.getAllModuleChildModuleResponse());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getallroles")
    public ResponseEntity<?>getAllRoles(){
        return success(roleService.getAllRoles());
    }


    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleCreateRequest request) {
        RoleResponse updated = roleService.updateRole(roleId, request);
        return success("Update Role Successfully",updated);
    }

    @PatchMapping("/roles/status/{id}")
    public ResponseEntity<?>updateStatus(@PathVariable Long id, @RequestBody UserStatusUpdateRequestDTO request){
            roleService.updateStatus(id,request.isActive());
            return success("Role status updated successfully");
    }

    // Added by Chetan
//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String userId,
            @RequestBody UserStatusUpdateRequestDTO request) {

        userService.updateUserActiveStatus(userId, request.isActive());
        return success("User status updated successfully");
    }


//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserListItemDTO> users = userService.getAllUsersList();
        return success(users);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id,
            @RequestBody PutUserRequestDTO dto) {

        UserListItemDTO updated = userService.putUserById(id, dto);
        return success("User updated successfully", updated);
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllDropdowns() {
        return success(dropdownService.getAllDropdowns());
    }

    @GetMapping("/editall")
    public ResponseEntity<?> getAllEditDropdowns(){
        return success(dropdownService.geteditdropdown());
    }
}
