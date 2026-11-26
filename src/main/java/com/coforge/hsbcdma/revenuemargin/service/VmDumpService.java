package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.VmDumpRecord;
import com.coforge.hsbcdma.revenuemargin.entity.VmDumpEntity;
import com.coforge.hsbcdma.revenuemargin.repository.VmDumpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VmDumpService {

    @Autowired
    private VmDumpRepository vmDumpRepository;
    public void deleteAllRecords() {vmDumpRepository.deleteAll();}
    public void saveRecords(
            List<VmDumpRecord> records) {

        for (VmDumpRecord record : records) {

            VmDumpEntity entity =
                    new VmDumpEntity();

            entity.setResourceId(
                    record.getResourceId()
            );

            entity.setResourceName(
                    record.getResourceName()
            );

            entity.setCtoolId(
                    record.getCToolId()
            );
            entity.setPricingModel(record.getPricingModel());
            entity.setEffectiveStartDate(record.getEffectiveStartDate());
            entity.setEffectiveEndDate(record.getEffectiveEndDate());
            entity.setJobCategoryGcbLevel(record.getJobCategoryGcbLevel());
            entity.setCtoolRate(record.getCtoolRate());
            entity.setFunctionalManager(record.getFunctionalManager());
            entity.setFunctionalManagerEmailId(record.getFunctionalManagerEmailId());
            entity.setEntityManager(record.getEntityManager());
            entity.setVendorName(record.getVendorName());
            entity.setEquivalentGcb(record.getEquivalentGcb());
            entity.setLegalEntity(record.getLegalEntity());
            entity.setCostCenter(record.getCostCenter());
            entity.setBfLevel3(record.getBfLevel3());
            entity.setBfLevel4(record.getBfLevel4());
            entity.setBaseLocation(record.getBaseLocation());
            entity.setPremises(record.getPremises());
            entity.setJobCategory(record.getJobCategory());
            entity.setOperatingUnit(record.getOperatingUnit());
            entity.setPoNumber(record.getPoNumber());
            entity.setContractStartDate(record.getContractStartDate());
            entity.setContractEndDate(record.getContractEndDate());
            entity.setCurrency(record.getCurrency());
            entity.setUnitOfMeasure(record.getUnitOfMeasure());
            entity.setRoleActive(record.getRoleActive());
            entity.setResourceActive(record.getResourceActive());
            entity.setLandedFlag(record.getLandedFlag());
            entity.setOnePo(record.getOnePo());
            vmDumpRepository.save(
                    entity
            );

        }

        System.out.println(
                "Records Saved = "
                        + records.size()
        );

    }
    public long getRecordCount() {

        return vmDumpRepository.count();

    }

}