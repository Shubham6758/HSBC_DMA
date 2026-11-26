package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.HsbcCentralResourceTrackerRecord;
import com.coforge.hsbcdma.revenuemargin.entity.HsbcCentralResourceTrackerEntity;
import com.coforge.hsbcdma.revenuemargin.repository.HsbcCentralResourceTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HsbcCentralResourceTrackerService {

    @Autowired
    private HsbcCentralResourceTrackerRepository
            hsbcCentralResourceTrackerRepository;

    public void deleteAllRecords() {
        hsbcCentralResourceTrackerRepository.deleteAll();
    }

    public void saveRecords(
            List<HsbcCentralResourceTrackerRecord> records) {
        System.out.println(
                "Vishal TRACKER saveRecords() called with "
                        + records.size()
                        + " records"
        );

        for (HsbcCentralResourceTrackerRecord record : records) {

            HsbcCentralResourceTrackerEntity entity =
                    new HsbcCentralResourceTrackerEntity();

            entity.setKey(record.getKey());
            entity.setCoforgeId(record.getCoforgeId());
            entity.setPsCode(record.getPsCode());
            entity.setEmployeeName(record.getEmployeeName());
            entity.setLob(record.getLob());
            entity.setNewLob(record.getNewLob());
            entity.setCoforgeBand(record.getCoforgeBand());
            entity.setHbu(record.getHbu());
            entity.setCoforgeLocation(record.getCoforgeLocation());
            entity.setNewLobForOthers(record.getNewLobForOthers());

            entity.setBusinessFunction(record.getBusinessFunction());
            entity.setSubBusinessFunction(record.getSubBusinessFunction());
            entity.setDepartment(record.getDepartment());
            entity.setFunctionalMgrHsbc(record.getFunctionalMgrHsbc());
            entity.setPrimarySkill(record.getPrimarySkill());
            entity.setExperience(record.getExperience());
            entity.setCtool(record.getCtool());
            entity.setCtoolLocation(record.getCtoolLocation());
            entity.setPricingModelHsbc(record.getPricingModelHsbc());
            entity.setJobCategory(record.getJobCategory());

            entity.setGcbLevel(record.getGcbLevel());
            entity.setCurrency(record.getCurrency());

            entity.setRate(record.getRate());
            entity.setEffectiveRate(record.getEffectiveRate());
            entity.setFixedCapacityRate(record.getFixedCapacityRate());

            entity.setEffectiveStartDate(record.getEffectiveStartDate());
            entity.setEffectiveEndDate(record.getEffectiveEndDate());

            entity.setResourceStatus(record.getResourceStatus());
            entity.setOnshoreOffshore(record.getOnshoreOffshore());
            entity.setPricingModelCoforge(record.getPricingModelCoforge());

            entity.setTentativeOnboardingDate(
                    record.getTentativeOnboardingDate()
            );

            entity.setWbsCode(record.getWbsCode());
            entity.setIpmsAllocation(record.getIpmsAllocation());
            entity.setGender(record.getGender());

            entity.setIpmsAllocationStartDate(
                    record.getIpmsAllocationStartDate()
            );

            entity.setIpmsAllocationEndDate(
                    record.getIpmsAllocationEndDate()
            );

            entity.setConcatenateForHeadcountNumber(
                    record.getConcatenateForHeadcountNumber()
            );

            entity.setConcatenateForHeadcountNewLob(
                    record.getConcatenateForHeadcountNewLob()
            );

            entity.setMonthOfRelease(
                    record.getMonthOfRelease()
            );

            entity.setDateOfExit(
                    record.getDateOfExit()
            );

            entity.setExitStatus(
                    record.getExitStatus()
            );

            entity.setReasonForExit(
                    record.getReasonForExit()
            );

            entity.setReplacementRequired(
                    record.getReplacementRequired()
            );

            entity.setRemarks(
                    record.getRemarks()
            );

            entity.setReplacementId(
                    record.getReplacementId()
            );

            entity.setReplacementName(
                    record.getReplacementName()
            );

            entity.setSubConVendor(
                    record.getSubConVendor()
            );

            entity.setLoc(
                    record.getLoc()
            );

            entity.setLobCheck(
                    record.getLobCheck()
            );

            entity.setCoforgeEmailId(
                    record.getCoforgeEmailId()
            );

            entity.setHsbcEmailId(
                    record.getHsbcEmailId()
            );

            entity.setSkillCluster(
                    record.getSkillCluster()
            );

            entity.setSubCluster(
                    record.getSubCluster()
            );

            hsbcCentralResourceTrackerRepository.save(
                    entity
            );

            hsbcCentralResourceTrackerRepository.save(
                    entity
            );
        }

        System.out.println(
                "Tracker Records Saved Vishal = "
                        + records.size()
        );
    }

    public long getRecordCount() {

        return hsbcCentralResourceTrackerRepository.count();

    }
}