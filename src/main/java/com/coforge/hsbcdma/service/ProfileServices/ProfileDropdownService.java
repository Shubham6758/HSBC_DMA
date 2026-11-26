package com.coforge.hsbcdma.service.ProfileServices;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileDropdownDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.CountryRefDTO;
import com.coforge.hsbcdma.repository.CountryCodeRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories.*;
import com.coforge.hsbcdma.repository.ProfileRepositories.*;
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


    private final OriginRepository originRepository;
    private final KaratStatusMasterRepository karatStatusMasterRepository;
    private final SourceMasterRepository sourceMasterRepository;
    private final OverallStatusMasterRepository overallStatusMasterRepository;

    private final PersonaMasterRepository personaMasterRepository;
    private final PrepStatusMasterRepository prepStatusMasterRepository;
    private final CoderpadResultMasterRepository coderpadResultMasterRepository;
    private final KaratReadyStatusMasterRepository karatReadyStatusMasterRepository;
    private final KaratTestResultMasterRepository karatTestResultMasterRepository;
    private final CountryCodeRepository countryCodeRepository;

    public ProfileDropdownDTO getProfileDropdowns() {

        ProfileDropdownDTO dto = new ProfileDropdownDTO();
        dto.setCountryCodes(
                countryCodeRepository.findAll()
                        .stream()
                        .map(c -> new CountryRefDTO(
                                c.getId(),
                                c.getName(),
                                c.getCallingCode()
                        ))
                        .toList()
        );
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


// ================= NEW DROPDOWNS =================

        dto.setOrigins(
                originRepository.findAll().stream()
                        .map(o -> new RefDTO(o.getId(), o.getName()))
                        .toList()
        );

        dto.setKaratStatuses(
                karatStatusMasterRepository.findAll().stream()
                        .map(k -> new RefDTO(k.getId(), k.getName()))
                        .toList()
        );

        dto.setSources(
                sourceMasterRepository.findAll().stream()
                        .map(s -> new RefDTO(s.getId(), s.getName()))
                        .toList()
        );

        dto.setOverallStatusRdgs(
                overallStatusMasterRepository.findAll().stream()
                        .map(o -> new RefDTO(o.getId(), o.getName()))
                        .toList()
        );

        // ================= ✅ NEW KARAT / PREP DROPDOWNS =================

        dto.setPersonas(
                personaMasterRepository.findAll().stream()
                        .map(p -> new RefDTO(p.getId(), p.getName()))
                        .toList()
        );

        dto.setPrepStatuses(
                prepStatusMasterRepository.findAll().stream()
                        .map(p -> new RefDTO(p.getId(), p.getName()))
                        .toList()
        );

        dto.setCoderpadResults(
                coderpadResultMasterRepository.findAll().stream()
                        .map(c -> new RefDTO(c.getId(), c.getName()))
                        .toList()
        );

        dto.setKaratReadyStatuses(
                karatReadyStatusMasterRepository.findAll().stream()
                        .map(k -> new RefDTO(k.getId(), k.getName()))
                        .toList()
        );

        dto.setKaratTestResults(
                karatTestResultMasterRepository.findAll().stream()
                        .map(k -> new RefDTO(k.getId(), k.getName()))
                        .toList()
        );

        return dto;
    }
}

