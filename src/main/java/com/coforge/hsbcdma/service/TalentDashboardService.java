package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.TalentDashboardDTO;
import com.coforge.hsbcdma.enums.Status;
import com.coforge.hsbcdma.repository.StatusCountView;
import com.coforge.hsbcdma.repository.TalentDashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TalentDashboardService {

    @Autowired
    private TalentDashboardRepository talentDashboardRepository;

    private static final Logger logger = LoggerFactory.getLogger(TalentDashboardService.class);

    public TalentDashboardDTO getTalentOverviewDetails(){

        TalentDashboardDTO talentDashboardDTO = new TalentDashboardDTO();

        Integer totalDemands = 0;
        List<StatusCountView> result =  talentDashboardRepository.countByStatuses(
                List.of(Status.OPEN_POSITIONS.getStatus(),Status.ON_BOARDING_INPROGRESS.getStatus(),Status.PROFILE_SHARED.getStatus()));


        for (StatusCountView row : result) {
            totalDemands =  totalDemands + row.getCount();
            talentDashboardDTO.setTotalDemands(totalDemands);
        }

        logger.info("Total demands in Talent Dashboard : {}", totalDemands);

        talentDashboardDTO.setOpenPositions(talentDashboardRepository.countByStatus(Status.OPEN_POSITIONS.getStatus()));
        talentDashboardDTO.setClosedPositions(talentDashboardRepository.countByStatus(Status.CLOSED_POSITIONS.getStatus()));
        talentDashboardDTO.setOnHold(talentDashboardRepository.countByStatus(Status.ON_HOLD.getStatus()));
        talentDashboardDTO.setAbandoned(talentDashboardRepository.countByStatus(Status.ABANDONED.getStatus()));
        talentDashboardDTO.setFulfilled(talentDashboardRepository.countByStatus(Status.FULFILLED.getStatus()));
        talentDashboardDTO.setOnBoardingInProgress(talentDashboardRepository.countByStatus(Status.ON_BOARDING_INPROGRESS.getStatus()));
        talentDashboardDTO.setProfileShared(talentDashboardRepository.countByStatus(Status.PROFILE_SHARED.getStatus()));
        talentDashboardDTO.setRejected(talentDashboardRepository.countByStatus(Status.REJECTED.getStatus()));
        talentDashboardDTO.setSoftSelect(talentDashboardRepository.countByStatus(Status.SOFT_SELECT.getStatus()));
        talentDashboardDTO.setCandidateResigned(talentDashboardRepository.countByStatus(Status.CANDIDATE_RESIGNED.getStatus()));
        talentDashboardDTO.setDuplicateDemand(talentDashboardRepository.countByStatus(Status.DUPLICATE_DEMAND.getStatus()));

        logger.info(talentDashboardDTO.toString());

        return  talentDashboardDTO;
    }
}
