package com.coforge.hsbcdma.service.UserManagementServices;

import com.coforge.hsbcdma.dto.RolemgmtDTO.DropdowneditResponse;
import com.coforge.hsbcdma.dto.RolemgmtDTO.GetAllModuleChildModuleResponse;
import com.coforge.hsbcdma.dto.UserMananagementDTO.DropdownResponse;
import com.coforge.hsbcdma.entity.Roles;
import com.coforge.hsbcdma.entity.SubDepartment;
import com.coforge.hsbcdma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DropdownService {
    @Autowired
    private CountryCodeRepository countryCodeRepository;
    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SubDepartmentRepository subDepartmentRepository;


    @Autowired
    RoleService roleService;
    public DropdownResponse getAllDropdowns(){
        DropdownResponse response = new DropdownResponse();

        response.setCountryCodes(countryCodeRepository.findAll());
        List<Roles> roles = rolesRepository.findByActiveTrue();
        List<DropdownResponse.RoleResponseDTO> roleDtos = roles.stream()
                .map(r -> new DropdownResponse.RoleResponseDTO(r.getId(), r.getRole()))
                .toList();

        response.setRoles(roleDtos);

        response.setLocations(locationRepository.findAll());
        response.setDepartments(departmentRepository.findAll());

        // map: departmentName → list of subDepartments
        Map<String, List<SubDepartment>> subDept = subDepartmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(sd -> sd.getDepartment().getName()));

        response.setSubDepartments(subDept);
        return response;
    }


    public DropdowneditResponse geteditdropdown(){
        List<Roles> roles = rolesRepository.findAll();
        List<DropdownResponse.RoleResponseDTO> roleDtos = roles.stream()
                .map(r -> new DropdownResponse.RoleResponseDTO(r.getId(), r.getRole()))
                .toList();
        List<GetAllModuleChildModuleResponse> modulesWithChildren =
                Optional.ofNullable(roleService.getAllModuleChildModuleResponse())
                        .orElseGet(Collections::emptyList);

        return DropdowneditResponse.builder()
                .rolesList(roleDtos)
                .getAllModuleChildModuleResponseList(modulesWithChildren)
                .build();
    }
}
