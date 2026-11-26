package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.entity.*;
import com.coforge.hsbcdma.revenuemargin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueMarginEnrichmentService {

    @Autowired
    private RdgMasterRepository rdgMasterRepository;
    @Autowired
    private VmDumpRepository vmDumpRepository;

    @Autowired
    private OtherDataRepository otherDataRepository;
    @Autowired
    private M4RateRepository m4RateRepository;

    @Autowired
    private HsbcCentralResourceTrackerRepository
            hsbcCentralResourceTrackerRepository;

    public String getCoforgeBand(
            String employeeNo) {
        RdgMasterEntity employee = rdgMasterRepository.findByEmployeeNo(employeeNo);
        if (employee == null) {return "";}
        return employee.getRoleBand();
    }
    public String getCountry(String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
        if (vmDumpRecords.isEmpty()) {return "";}
        String legalEntity = vmDumpRecords.get(0).getLegalEntity();
        if ("HBUK".equalsIgnoreCase(legalEntity) || "HGSU".equalsIgnoreCase(legalEntity)) {return "UNITED KINGDOM";}
        if ("HSDI".equalsIgnoreCase(legalEntity)) {return "INDIA";}
        return "";
    }
    public String getGbGi(String ctool) {
        List<HsbcCentralResourceTrackerEntity> trackers = hsbcCentralResourceTrackerRepository.findAllByCtool(ctool);
        if (trackers.isEmpty()) {return "";}
        return trackers.get(0).getNewLob();
    }
    //public String getBfLevel3(String ctool) {
    //    List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByCtoolId(ctool);
    //    if (vmDumpRecords.isEmpty()) {return "";}
    //    return vmDumpRecords.get(0).getBfLevel3();
    //}
    //public String getBfLevel4(String ctool) {
    //    List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByCtoolId(ctool);
    //    if (vmDumpRecords.isEmpty()) {return "";}
    //    return vmDumpRecords.get(0).getBfLevel4();
    //}
    //this get below is for bflevel3 and bflevel4 combined
    public VmDumpEntity getVmDumpByCtool(String ctool) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByCtoolId(ctool);
        if (vmDumpRecords.isEmpty()) {return null;}
        return vmDumpRecords.get(0);
    }
    //public String getFteSubConFromResourceTracker(String psid, String resourceStatus) {
    //    String key = psid + resourceStatus;
    //    HsbcCentralResourceTrackerEntity tracker = hsbcCentralResourceTrackerRepository
    //                    .findByKey(key);
    //    if (tracker == null) {return "";}
    //    String coforgeBand = tracker.getCoforgeBand();
    //    if ("0".equals(coforgeBand)) {return "Seconded/Outsource";}
    //    return "Regular Staff Member";
    //}
    public String getCoforgeBandFromResourceTracker(String psid) {
        //String key = psid + resourceStatus;
        List<HsbcCentralResourceTrackerEntity> trackers = hsbcCentralResourceTrackerRepository.findAllByPsCode(psid);
        if (trackers.isEmpty()) {return "";}
        return trackers.get(0).getCoforgeBand();
    }
    //public String getPricingModel(String psid) {
    //    HsbcCentralResourceTrackerEntity tracker = hsbcCentralResourceTrackerRepository.findByKey(psid + "Active");
    //    if (tracker == null) {return "";}
    //    return tracker.getPricingModelHsbc();
    //}
    public String getPricingModel(String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
        if (vmDumpRecords.isEmpty()) {return "";}
        VmDumpEntity vmDump = vmDumpRecords.get(0);
        return vmDump.getPricingModel();
    }
    public LocalDate getEffectiveStartDate(String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
            if (!vmDumpRecords.isEmpty()) {
                VmDumpEntity vmDump = vmDumpRecords.get(0);
                if (vmDump.getEffectiveStartDate() != null) {
                    return vmDump.getEffectiveStartDate();
                }
            }
        OtherDataEntity otherData = otherDataRepository.findByPsid(psid);

        if (otherData != null && otherData.getContractStartDate() != null) {
            return otherData.getContractStartDate();
        }
        return null;
    }
    public LocalDate getEffectiveEndDate(String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
        if (!vmDumpRecords.isEmpty()) {
            VmDumpEntity vmDump =  vmDumpRecords.get(0);
            if (vmDump.getEffectiveEndDate() != null) {return vmDump.getEffectiveEndDate();
            }
        }
        OtherDataEntity otherData = otherDataRepository.findByPsid(psid);
        if (otherData != null && otherData.getContractEndDate() != null) {return otherData.getContractEndDate();}
        return null;
    }
    public String getResourceStatus(String psid) {
        LocalDate effectiveEndDate = getEffectiveEndDate(psid);
        if (effectiveEndDate == null) {return "";}
        if (effectiveEndDate.isBefore(LocalDate.now())) {return "Non-Active";}
        return "Active";
    }
    public String getFteSubCon(String employeeNo) {
        RdgMasterEntity employee = rdgMasterRepository.findByEmployeeNo(employeeNo);
        if (employee == null) {return "";}
        return employee.getEmployeeType();
    }
    public String getJobCategoryGcbLevel(
            String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
        if (vmDumpRecords.isEmpty()) {return "";}
        VmDumpEntity vmDump = vmDumpRecords.get(0);
        return vmDump.getJobCategoryGcbLevel();
    }
    public BigDecimal getCToolRate(String psid) {
        List<VmDumpEntity> vmDumpRecords = vmDumpRepository.findByResourceId(psid);
        if (vmDumpRecords.isEmpty()) {return BigDecimal.ZERO;}
        VmDumpEntity vmDump = vmDumpRecords.get(0);
        return vmDump.getCtoolRate();
    }
    public BigDecimal getM4Rate(String jobCategoryGcbLevel) {
        M4RateEntity m4Rate = m4RateRepository.findByRoleTitle(jobCategoryGcbLevel);
        if (m4Rate == null) {return BigDecimal.ZERO;}
        return m4Rate.getMaxDayRateUsd();
    }
    public BigDecimal getEffectiveRate(
            String pricingModel,
            String name,
            String gbGi,
            BigDecimal ctoolRate,
            BigDecimal m4Rate) {
        if (ctoolRate == null) {ctoolRate = BigDecimal.ZERO;}
        if (m4Rate == null) {m4Rate = BigDecimal.ZERO;}
        if ("Time and material (SoW)".equals(pricingModel)) {return m4Rate;}
        if ("Pandit,Saurav".equals(name)) {return ctoolRate;}
        if ("CTO".equals(gbGi)) {return m4Rate;}
        if (ctoolRate.compareTo(m4Rate) < 0) {return ctoolRate;}
        if ("UK Technology".equals(gbGi)) {return m4Rate;}
        if ("IWPB Technology".equals(gbGi)) {return m4Rate;}
        if ("Group Functions Technology".equals(gbGi)) {return m4Rate;}
        if (ctoolRate.compareTo(BigDecimal.ZERO) == 0) {return m4Rate;}
        if (m4Rate.compareTo(ctoolRate) < 0) {return ctoolRate.multiply(new BigDecimal("0.8"));}
        return m4Rate;
    }
}