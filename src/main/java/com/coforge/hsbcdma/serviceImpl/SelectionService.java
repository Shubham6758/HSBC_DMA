package com.coforge.hsbcdma.serviceImpl;

import com.coforge.hsbcdma.dto.OnboardingOLDDTO;
import com.coforge.hsbcdma.dto.SelectionDTO;
import com.coforge.hsbcdma.entity.Demand;
import com.coforge.hsbcdma.entity.OnboardingOLD;
import com.coforge.hsbcdma.entity.ProfileTrackerOLD;
import com.coforge.hsbcdma.entity.enums.DemandStatus;
import com.coforge.hsbcdma.mapper.EntityDtoMapper;
import com.coforge.hsbcdma.repository.DemandRepository;
import com.coforge.hsbcdma.repository.OnboardingOldRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * SelectionService handles PMO selection and onboarding update.
 */
@Service
@Transactional
public class SelectionService {
    private static final Logger log = LoggerFactory.getLogger(SelectionService.class);
    private final DemandRepository demandRepository;
    private final ProfileTrackerRepository profileRepository;
    private final OnboardingOldRepository onboardingOldRepository;

    public SelectionService(DemandRepository demandRepository, ProfileTrackerRepository profileRepository, OnboardingOldRepository onboardingOldRepository) {
        this.demandRepository = demandRepository;
        this.profileRepository = profileRepository;
        this.onboardingOldRepository = onboardingOldRepository;
    }

    /**
     * Select a candidate for a demand and create onboarding.
     * Created by: Pooja.I
     */
    public OnboardingOLDDTO select(Long demandId, SelectionDTO selection) {
        Demand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + demandId));
        ProfileTrackerOLD profile = profileRepository.findById(selection.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + selection.getProfileId()));
        if (profile.getDemandId()==null ) {
            throw new IllegalArgumentException("Profile does not belong to the specified demand");
        }
        /*if(profile.getStatus() != ProfileStatus.ACCEPTED){
            throw new IllegalArgumentException("Profile must be ACCEPTED before onboarding");
        }*/
        Optional<OnboardingOLD> existing = onboardingOldRepository.findByDemandIdAndProfileId(demand.getId() , profile.getId());
        if(existing.isPresent()){
          //return EntityDtoMapper.toOnboardingDTO(existing.get());
            throw new IllegalArgumentException("The profile for given demand is already onboarded");
        }
        OnboardingOLD onboarding = new OnboardingOLD();
        onboarding.setDemand(demand);
        //onboarding.setProfile(profile);
        onboarding.setNotes(selection.getNotes());
        OnboardingOLD saved = onboardingOldRepository.save(onboarding);
        //profile.setStatus(ProfileStatus.SELECTED);
        profileRepository.save(profile);
        demand.setStatus(DemandStatus.SELECTED);
        demandRepository.save(demand);
        log.info("Candidate selected for demandId={} profileId={} onboardingId={}", demand.getId(), profile.getId(), saved.getId());
        return EntityDtoMapper.toOnboardingDTO(saved);
    }
}
