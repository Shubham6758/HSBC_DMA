package com.coforge.hsbcdma.mapper;


import com.coforge.hsbcdma.dto.*;
import com.coforge.hsbcdma.dto.UserMananagementDTO.RoleDTO;
import com.coforge.hsbcdma.entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Central DTO <-> Entity mapper to avoid redundancy.
 */
public final class EntityDtoMapper {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;
    private EntityDtoMapper() {}

    /** Created by: Pooja.I */
    public static Demand toDemandEntity(DemandDTO dto) {
        Demand d = new Demand();
        d.setId(dto.getId());
        d.setDemandBusinessId(dto.getDemandBusinessId());
        d.setRr(dto.getRr());
        d.setLob(dto.getLob());
        d.setHiringManager(dto.getHiringManager());
        d.setSkillCluster(dto.getSkillCluster());
        d.setPrimarySkill(dto.getPrimarySkill());
        d.setSecondarySkill(dto.getSecondarySkill());
        d.setExternality(dto.getExternality());
        d.setStatus(dto.getStatus());
        if (dto.getDateProfileShared()!=null) {
            d.setDateProfileShared(LocalDateTime.parse(dto.getDateProfileShared(), ISO));
        }
        return d;
    }

    /** Created by: Pooja.I */
    public static DemandDTO toDemandDTO(Demand entity) {
        DemandDTO dto = new DemandDTO();
        dto.setId(entity.getId());
        dto.setDemandBusinessId(entity.getDemandBusinessId());
        dto.setRr(entity.getRr());
        dto.setLob(entity.getLob());
        dto.setHiringManager(entity.getHiringManager());
        dto.setSkillCluster(entity.getSkillCluster());
        dto.setPrimarySkill(entity.getPrimarySkill());
        dto.setSecondarySkill(entity.getSecondarySkill());
        dto.setExternality(entity.getExternality());
        dto.setStatus(entity.getStatus());
        if (entity.getDateProfileShared()!=null) {
            dto.setDateProfileShared(entity.getDateProfileShared().format(ISO));
        }
        return dto;
    }

  /*  *//** Created by: Pooja.I *//*
    public static Profile toProfileEntity(ProfileTrackerDTO dto, Demand demand) {
        Profile p = new Profile();
        p.setId(dto.getDemandId());
        p.setCandidateName(dto.getCandidateName());
        p.setPrimarySkill(dto.getPrimarySkill());
        p.setSecondarySkill(dto.getSecondarySkill());
        p.setStatus(dto.getStatus());
        p.setDemand(demand);
        return p;
    }*/

 /*   *//** Created by: Pooja.I *//*
    public static ProfileDTO toProfileDTO(Profile entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setCandidateName(entity.getCandidateName());
        dto.setPrimarySkill(entity.getPrimarySkill());
        dto.setSecondarySkill(entity.getSecondarySkill());
        dto.setStatus(entity.getStatus());
        dto.setDemandId(entity.getDemand()!=null ? entity.getDemand().getId() : null);
        return dto;
    }*/

    /** Created by: Pooja.I */
    public static OnboardingOLDDTO toOnboardingDTO(OnboardingOLD entity) {
        OnboardingOLDDTO dto = new OnboardingOLDDTO();
        dto.setId(entity.getId());
        dto.setDemandId(entity.getDemand().getId());
        dto.setProfileId(entity.getProfile().getId());
        dto.setSelectedAt(entity.getSelectedAt().format(ISO));
        dto.setNotes(entity.getNotes());
        return dto;
    }


    /** Created by: Bhushan K */
    public static RdgManagementResponseDto toRdgManagementDTO(RdgManagement entity, String base64) {
        RdgManagementResponseDto dto = new RdgManagementResponseDto();
        dto.setId(entity.getId());
        dto.setDemandId(entity.getDemandId());
        dto.setPrimarySkills(entity.getPrimarySkills());
        dto.setSecondarySkills(entity.getSecondarySkills());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setNameOfCandidate(entity.getNameOfCandidate());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setBriefSummary(entity.getBriefSummary());
        dto.setLocation(entity.getLocation());
        dto.setCvFileName(entity.getCvFileName());
        dto.setCvFileBase64(base64);
        return dto;
    }

    /** Created by: Bhushan K */
    public static TaManagementResponseDto toTaManagementDTO(TaManagement entity, String cvBase64, String irsBase64) {
        TaManagementResponseDto dto = new TaManagementResponseDto();
        dto.setId(entity.getId());
        dto.setDemandId(entity.getDemandId());
        dto.setPrimarySkills(entity.getPrimarySkills());
        dto.setSecondarySkills(entity.getSecondarySkills());
        dto.setNameOfCandidate(entity.getNameOfCandidate());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setBriefSummary(entity.getBriefSummary());
        dto.setLocation(entity.getLocation());
        dto.setCvFileName(entity.getCvFileName());
        dto.setCvFileBase64(cvBase64);
        dto.setIrsFileName(entity.getIrsFileName());
        dto.setIrsFileBase64(irsBase64);
        return dto;
    }

    public static Roles toRoleEntity(RoleDTO.RoleCreateRequest req){
//        return Roles.builder()
//                .name(req.name())
//                .description(req.description())
//                .build();

        Roles roles = new Roles();
        roles.setRole(req.name());
        return roles;
    }

    public static RoleDTO.RoleCreateResponse toRoleDTO(Roles role){
        return new RoleDTO.RoleCreateResponse(
                role.getId(),
                role.getRole()
        );
    }


    public static RoleDTO.RoleResponse toRoleResponse(Roles r) {
        return new RoleDTO.RoleResponse(
                r.getId(),
                r.getRole()
        );
    }



    public static AuthResponseDTO.RolesDTO toLoginRoleDTO(Roles role){
        return new AuthResponseDTO.RolesDTO(role.getModuleChildModule(),role.getRole());
    }
}
