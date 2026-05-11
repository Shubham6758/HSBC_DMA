package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.ProfileTrackerDTO;
import com.coforge.hsbcdma.entity.ProfileTrackerOLD;
import com.coforge.hsbcdma.exception.DMAGenericRuntimeException;
import com.coforge.hsbcdma.repository.ProfileTrackerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProfileTrackerService handles profile update and get.
 * pratish.b
 */
@Service
@Transactional
public class ProfileTrackerService {

    private static final Logger log = LoggerFactory.getLogger(ProfileTrackerService.class);

    @Autowired
    private ProfileTrackerRepository profileTrackerRepository;

    /**
     *
      * This method retrieves Profile based on demandId.
     * @return ProfileTrackerDTO
     * Created-by : pratish.b
     */
    public ProfileTrackerDTO getProfileByDemandId(String demandId){

       Optional<ProfileTrackerOLD> profile = profileTrackerRepository.findByDemandId(demandId);
       if(!profile.isPresent()){
           throw new RuntimeException("Invalid demand Id.");
       }
        ProfileTrackerOLD profileTracker = profile.get();

        ProfileTrackerDTO profileTrackerDTO = entityToDTO(profileTracker);
        return profileTrackerDTO;
    }

    /**
     * This method updates Profile based for the particular demandId.
     * Created-by : pratish.b
     */
    public void  updateProfileTracker(ProfileTrackerDTO dto){

        Optional<ProfileTrackerOLD> profileTracker = profileTrackerRepository.findByDemandId(dto.getDemandId());

        if(!profileTracker.isPresent()){
            throw new RuntimeException("Invalid demand Id.");
        }
        ProfileTrackerOLD profile = profileTracker.get();
        profile.setCurrentProfileShared(dto.getCurrentProfileShared());
        profile.setProfileSharedDate(dto.getProfileSharedDate());
        profile.setExternalInternal(dto.getExternalInternal());
        profile.setInterviewDate(dto.getInterviewDate());
        profile.setStatus(dto.getStatus());
        profile.setDecisionDate(dto.getDecisionDate());
        profile.setP1Age(dto.getP1Age());

        profileTrackerRepository.save(profile);
    }

    /**
     * This method retrieves Profiles between start and end date.
     * Created-by : pratish.b
     */
    public List<ProfileTrackerDTO> retrieveProfilesOnStartAndENdDate(LocalDate startDate, LocalDate endDate){

        List<ProfileTrackerDTO> profileTrackerDTOList = new ArrayList<>();
        List<ProfileTrackerOLD> profileTrackerList = profileTrackerRepository.findByProfileSharedDateBetween(startDate, endDate);
        if(profileTrackerList == null || profileTrackerList.isEmpty()){
            throw new DMAGenericRuntimeException("No profiles available for the specified dates.");
        }

        for(ProfileTrackerOLD profileTracker : profileTrackerList){
            profileTrackerDTOList.add(entityToDTO(profileTracker));
        }

        return profileTrackerDTOList;
    }

    /**
     * Entity to DTO conversion
     * Created-by : pratish.b
     */
    private ProfileTrackerDTO entityToDTO(ProfileTrackerOLD profileTracker){

        ProfileTrackerDTO profileTrackerDTO = new ProfileTrackerDTO();

        profileTrackerDTO.setId(profileTracker.getId());//Primary key
        profileTrackerDTO.setDemandId(profileTracker.getDemandId());
        profileTrackerDTO.setRrNumber(profileTracker.getRrNumber());
        profileTrackerDTO.setLob(profileTracker.getLob());
        profileTrackerDTO.setHiringManager(profileTracker.getHiringManager());
        profileTrackerDTO.setSkillCluter(profileTracker.getSkillCluter());
        profileTrackerDTO.setPrimarySkills(profileTracker.getPrimarySkills());
        profileTrackerDTO.setSecondarySkills(profileTracker.getSecondarySkills());
        profileTrackerDTO.setCurrentProfileShared(profileTracker.getCurrentProfileShared());
        profileTrackerDTO.setProfileSharedDate(profileTracker.getProfileSharedDate());
        profileTrackerDTO.setExternalInternal(profileTracker.getExternalInternal());
        profileTrackerDTO.setInterviewDate(profileTracker.getInterviewDate());
        profileTrackerDTO.setStatus(profileTracker.getStatus());
        profileTrackerDTO.setDecisionDate(profileTracker.getDecisionDate());
        profileTrackerDTO.setP1Age(profileTracker.getP1Age());

        return profileTrackerDTO;
    }


    /**
     * Upload a profile against a demand.
     * Created by: Pooja.I
     */
  /*  public ProfileTrackerDTO upload(ProfileTrackerDTO dto) {
        Demand demand = demandRepository.findById(dto.getDemandId())
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + dto.getDemandId()));
        Profile profile = EntityDtoMapper.toProfileEntity(dto, demand);
        if(demand.getStatus() == DemandStatus.CLOSED){
            throw  new IllegalArgumentException("Demand has been closed , cannot upload profiles");
        }
        demand.setDateProfileShared(LocalDateTime.now());
        Profile saved = profileRepository.save(profile);
        return EntityDtoMapper.toProfileDTO(saved);
    }*/

    /**
     * List profiles for a demand.
     * Created by: Pooja.I
     */
/*    @Transactional(readOnly = true)
    public List<ProfileTrackerDTO> listByDemand(Long demandId) {
        return profileRepository.findByDemand_Id(demandId).stream()
                .map(EntityDtoMapper::toProfileDTO)
                .collect(Collectors.toList());
    }*/

    /**
     * DM decision: accept or reject a profile.
     * Created by: Pooja.I
     */
/*    public ProfileTrackerDTO decide(Long profileId, DecisionDTO decision) {
        Profile p = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profileId));
        if (decision.getDecision() == ProfileStatus.ACCEPTED || decision.getDecision() == ProfileStatus.REJECTED) {
            p.setStatus(decision.getDecision());
        } else {
            throw new IllegalArgumentException("Invalid decision; must be ACCEPTED or REJECTED");
        }
        log.info("Profile decision updated id={} status={}", p.getId(), p.getStatus());
        return EntityDtoMapper.toProfileDTO(p);
    }*/
}
