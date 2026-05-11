package com.coforge.hsbcdma.service.ProfileServices;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileDropdownDTO;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileStatusRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileDropdownService {

    private final LocationRepository locationRepository;
    private final SkillClusterRepository skillClusterRepository;
    private final HbuRepository hbuRepository;
    private final ExternalInternalRepository externalInternalRepository;
    private final PrimarySkillsRepository primarySkillsRepository;
    private final SecondarySkillsRepository secondarySkillsRepository;
    private final ProfileStatusRepository profileStatusRepository;

    public ProfileDropdownDTO getProfileDropdowns() {

        ProfileDropdownDTO dto = new ProfileDropdownDTO();

        dto.setLocations(
                locationRepository.findAll()
                        .stream()
                        .map(l -> new RefDTO(l.getId(), l.getName()))
                        .toList()
        );

        dto.setSkillClusters(
                skillClusterRepository.findAll()
                        .stream()
                        .map(s -> new RefDTO(s.getId(), s.getName()))
                        .toList()
        );

        dto.setHbus(
                hbuRepository.findAll()
                        .stream()
                        .map(h -> new RefDTO(h.getId(), h.getName()))
                        .toList()
        );

        dto.setExternalInternals(
                externalInternalRepository.findAll()
                        .stream()
                        .map(e -> new RefDTO(e.getId(), e.getName()))
                        .toList()
        );

        dto.setProfileStatusList(
                profileStatusRepository.findAll()
                        .stream()
                        .map(e -> new RefDTO(e.getId(),e.getName()))
                        .toList()
        );

        dto.setPrimarySkills(
                primarySkillsRepository.findAll()
                        .stream()
                        .map(p -> new RefDTO(p.getId(), p.getName()))
                        .toList()
        );

        dto.setSecondarySkills(
                secondarySkillsRepository.findAll()
                        .stream()
                        .map(s -> new RefDTO(s.getId(), s.getName()))
                        .toList()
        );

        return dto;
    }
}

