package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.RdgMasterRecord;
import com.coforge.hsbcdma.revenuemargin.entity.RdgMasterEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RdgMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RdgMasterService {

    @Autowired
    private RdgMasterRepository rdgMasterRepository;
    public void deleteAllRecords() {

        rdgMasterRepository.deleteAll();

    }
    public long getRecordCount() {
        return rdgMasterRepository.count();
    }
    public void saveRecords(List<RdgMasterRecord> records) {

        for (RdgMasterRecord record : records) {

            RdgMasterEntity entity =
                    new RdgMasterEntity();

            entity.setEmployeeNo(record.getEmployeeNo());
            entity.setBaseEmployeeNo(record.getBaseEmployeeNo());
            entity.setName(record.getName());
            entity.setEmail(record.getEmail());

            entity.setEmployeeType(record.getEmployeeType());
            entity.setEmployeeSubGroup(record.getEmployeeSubGroup());
            entity.setTraineeCategory(record.getTraineeCategory());
            entity.setGender(record.getGender());
            entity.setOnsiteOffshore(record.getOnsiteOffshore());

            entity.setCompanyCode(record.getCompanyCode());
            entity.setCompanyName(record.getCompanyName());
            entity.setCountry(record.getCountry());

            entity.setPaCode(record.getPaCode());
            entity.setPaName(record.getPaName());
            entity.setPsa(record.getPsa());

            entity.setRoleCode(record.getRoleCode());
            entity.setRoleDescription(record.getRoleDescription());
            entity.setDesignation(record.getDesignation());
            entity.setDesignationLevel(record.getDesignationLevel());
            entity.setRoleBand(record.getRoleBand());

            entity.setDis(record.getDis());
            entity.setDisp(record.getDisp());
            entity.setClassifier(record.getClassifier());

            entity.setEmpPractice(record.getEmpPractice());
            entity.setEmployeeGroupPractice(record.getEmployeeGroupPractice());

            entity.setSupervisorEmpcode(record.getSupervisorEmpcode());
            entity.setSupervisorName(record.getSupervisorName());

            entity.setCareerManagerCode(record.getCareerManagerCode());
            entity.setCareerManagerName(record.getCareerManagerName());

            entity.setDateOfJoining(record.getDateOfJoining());

            entity.setOrganizationalUnit(record.getOrganizationalUnit());
            entity.setOrganizationalUnitName(record.getOrganizationalUnitName());

            entity.setIpmsParentWbsCode(record.getIpmsParentWbsCode());
            entity.setIpmsParentWbsName(record.getIpmsParentWbsName());

            entity.setIpmsWbsCode(record.getIpmsWbsCode());
            entity.setIpmsWbsName(record.getIpmsWbsName());

            entity.setWbsType(record.getWbsType());

            entity.setIpmsAllocationFromDate(record.getIpmsAllocationFromDate());
            entity.setIpmsAllocationToDate(record.getIpmsAllocationToDate());

            entity.setForcedAllocation(record.getForcedAllocation());

            entity.setCostCentre(record.getCostCentre());
            entity.setCostCentreName(record.getCostCentreName());

            entity.setProjectPracticeCode(record.getProjectPracticeCode());
            entity.setProjectPractice(record.getProjectPractice());
            entity.setProjectGroupPractice(record.getProjectGroupPractice());

            entity.setIbu(record.getIbu());
            entity.setSubIbu(record.getSubIbu());

            entity.setEmpHorizontal(record.getEmpHorizontal());
            entity.setProjectHorizontal(record.getProjectHorizontal());

            entity.setCostGroup(record.getCostGroup());
            entity.setCostGroupDesc(record.getCostGroupDesc());
            entity.setBuCostGroup(record.getBuCostGroup());

            entity.setCustomerCode(record.getCustomerCode());
            entity.setCustomerName(record.getCustomerName());

            entity.setAccountCustomerGroup(record.getAccountCustomerGroup());

            entity.setPrevEmployeeCode(record.getPrevEmployeeCode());

            entity.setPrevDoj(record.getPrevDoj());
            entity.setContractEndDate(record.getContractEndDate());

            entity.setSkillArea(record.getSkillArea());
            entity.setSkillCluster(record.getSkillCluster());
            entity.setSubCluster(record.getSubCluster());

            entity.setProvisionalIbu(record.getProvisionalIbu());
            entity.setProvisionalSubBu(record.getProvisionalSubBu());

            entity.setBilledUnbilled(record.getBilledUnbilled());
            entity.setItBpsHc(record.getItBpsHc());

            entity.setGeo(record.getGeo());
            entity.setLocation(record.getLocation());
            entity.setOrgKey(record.getOrgKey());

            rdgMasterRepository.save(entity);
        }

        System.out.println(
                "RDG Records Saved = "
                        + records.size()
        );
    }
}