package com.coforge.hsbcdma.resource.Service_Resource_Sheet;

import com.coforge.hsbcdma.resource.Entity_Resource_Sheet.ResourceData;
import com.coforge.hsbcdma.resource.Repository_Resource_sheet.ResourceDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceDataService {

    private final ResourceDataRepository repository;

    public ResourceData save(ResourceData data) {

        System.out.println("---- SAVE CALLED ----");
        System.out.println("Incoming PSID: " + data.getPsid());

        if (data.getPsid() != null) {

            ResourceData existingActive = repository
                    .findTopByPsidAndIsActiveTrueOrderByUpdatedAtDescCreatedAtDesc(data.getPsid())
                    .orElse(null);

            if (existingActive != null) {
                if (isSameBusinessData(existingActive, data)) {
                    System.out.println("No data change found for PSID: " + data.getPsid() + ". Skipping insert.");
                    return existingActive;
                }

                List<ResourceData> existing =
                        repository.findByPsidAndIsActiveTrueOrderByCreatedAtDesc(data.getPsid());

                System.out.println("Existing active rows found: " + existing.size());

                for (ResourceData old : existing) {
                    System.out.println("Deactivating ID: " + old.getId());
                    old.setIsActive(false);
                    repository.save(old);
                }
            }
        }

        if (data.getIsActive() == null) {
            data.setIsActive(true);
        }

        ResourceData saved = repository.save(data);

        System.out.println("New row saved ID: " + saved.getId());

        return saved;
    }

    private boolean isSameBusinessData(ResourceData a, ResourceData b) {
        return sameText(a.getPsid(), b.getPsid())
                && sameText(a.getResourceName(), b.getResourceName())
                && sameText(a.getLob(), b.getLob())
                && sameText(a.getNewLob(), b.getNewLob())
                && sameText(a.getNewLobForOthers(), b.getNewLobForOthers())
                && sameText(a.getBusinessFunction(), b.getBusinessFunction())
                && sameText(a.getSubBusinessFunction(), b.getSubBusinessFunction())
                && sameText(a.getDepartment(), b.getDepartment())
                && sameText(a.getFunctionalMgrHsbc(), b.getFunctionalMgrHsbc())
                && sameText(a.getPrimarySkill(), b.getPrimarySkill())
                && sameBigDecimal(a.getExperience(), b.getExperience())
                && sameText(a.getCtool(), b.getCtool())
                && sameText(a.getCtoolLocation(), b.getCtoolLocation())
                && sameText(a.getPricingModelHsbc(), b.getPricingModelHsbc())
                && sameText(a.getJobCategory(), b.getJobCategory())
                && sameText(a.getGcbLevel(), b.getGcbLevel())
                && sameText(a.getCurrency(), b.getCurrency())
                && sameBigDecimal(a.getRate(), b.getRate())
                && sameBigDecimal(a.getEffectiveRate(), b.getEffectiveRate())
                && sameBigDecimal(a.getFixedCapacityRate(), b.getFixedCapacityRate())
                && java.util.Objects.equals(a.getEffectiveStartDate(), b.getEffectiveStartDate())
                && java.util.Objects.equals(a.getEffectiveEndDate(), b.getEffectiveEndDate())
                && sameText(a.getEmpId(), b.getEmpId())
                && sameText(a.getResourceStatus(), b.getResourceStatus())
                && sameText(a.getOnshoreOffshore(), b.getOnshoreOffshore())
                && sameText(a.getPricingModelCoforge(), b.getPricingModelCoforge())
                && java.util.Objects.equals(a.getTentativeOnboardingDate(), b.getTentativeOnboardingDate())
                && sameText(a.getWbsCode(), b.getWbsCode())
                && sameText(a.getCoforgeBand(), b.getCoforgeBand())
                && sameText(a.getHbu(), b.getHbu())
                && sameText(a.getIpmsAllocation(), b.getIpmsAllocation())
                && sameText(a.getGender(), b.getGender())
                && sameText(a.getCoforgeLocation(), b.getCoforgeLocation())
                && java.util.Objects.equals(a.getIpmsAllocationStartDate(), b.getIpmsAllocationStartDate())
                && java.util.Objects.equals(a.getIpmsAllocationEndDate(), b.getIpmsAllocationEndDate())
                && sameText(a.getConcatHeadcountNumber(), b.getConcatHeadcountNumber())
                && sameText(a.getConcatHeadcountNewLob(), b.getConcatHeadcountNewLob())
                && sameText(a.getMonthOfRelease(), b.getMonthOfRelease())
                && java.util.Objects.equals(a.getDateOfExit(), b.getDateOfExit())
                && sameText(a.getExitStatus(), b.getExitStatus())
                && sameText(a.getReasonForExit(), b.getReasonForExit())
                && sameText(a.getReplacementRequired(), b.getReplacementRequired())
                && sameText(a.getRemarks(), b.getRemarks())
                && sameText(a.getReplacementGiven(), b.getReplacementGiven())
                && sameText(a.getReplacementId(), b.getReplacementId())
                && sameText(a.getReplacementName(), b.getReplacementName());
    }

    private boolean sameText(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean sameBigDecimal(java.math.BigDecimal a, java.math.BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }



    public List<ResourceData> getAll() {
        return repository.findAllByOrderByIsActiveDescUpdatedAtDescCreatedAtDesc();
    }

    public ResourceData getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResourceData not found with id: " + id));
    }

    public ResourceData update(Long id, ResourceData updatedData) {
        ResourceData existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResourceData not found with id: " + id));

        copyAllFields(updatedData, existing);

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void copyAllFields(ResourceData src, ResourceData target) {
        target.setPsid(src.getPsid());
        target.setResourceName(src.getResourceName());
        target.setLob(src.getLob());
        target.setNewLob(src.getNewLob());
        target.setNewLobForOthers(src.getNewLobForOthers());
        target.setBusinessFunction(src.getBusinessFunction());
        target.setSubBusinessFunction(src.getSubBusinessFunction());
        target.setDepartment(src.getDepartment());
        target.setFunctionalMgrHsbc(src.getFunctionalMgrHsbc());
        target.setPrimarySkill(src.getPrimarySkill());
        target.setExperience(src.getExperience());
        target.setCtool(src.getCtool());
        target.setCtoolLocation(src.getCtoolLocation());
        target.setPricingModelHsbc(src.getPricingModelHsbc());
        target.setJobCategory(src.getJobCategory());
        target.setGcbLevel(src.getGcbLevel());
        target.setCurrency(src.getCurrency());
        target.setRate(src.getRate());
        target.setEffectiveRate(src.getEffectiveRate());
        target.setFixedCapacityRate(src.getFixedCapacityRate());
        target.setEffectiveStartDate(src.getEffectiveStartDate());
        target.setEffectiveEndDate(src.getEffectiveEndDate());
        target.setEmpId(src.getEmpId());
        target.setResourceStatus(src.getResourceStatus());
        target.setOnshoreOffshore(src.getOnshoreOffshore());
        target.setPricingModelCoforge(src.getPricingModelCoforge());
        target.setTentativeOnboardingDate(src.getTentativeOnboardingDate());
        target.setWbsCode(src.getWbsCode());
        target.setCoforgeBand(src.getCoforgeBand());
        target.setHbu(src.getHbu());
        target.setIpmsAllocation(src.getIpmsAllocation());
        target.setGender(src.getGender());
        target.setCoforgeLocation(src.getCoforgeLocation());
        target.setIpmsAllocationStartDate(src.getIpmsAllocationStartDate());
        target.setIpmsAllocationEndDate(src.getIpmsAllocationEndDate());
        target.setConcatHeadcountNumber(src.getConcatHeadcountNumber());
        target.setConcatHeadcountNewLob(src.getConcatHeadcountNewLob());
        target.setMonthOfRelease(src.getMonthOfRelease());
        target.setDateOfExit(src.getDateOfExit());
        target.setExitStatus(src.getExitStatus());
        target.setReasonForExit(src.getReasonForExit());
        target.setReplacementRequired(src.getReplacementRequired());
        target.setRemarks(src.getRemarks());
        target.setReplacementGiven(src.getReplacementGiven());
        target.setReplacementId(src.getReplacementId());
        target.setReplacementName(src.getReplacementName());

        if (src.getIsActive() != null) {
            target.setIsActive(src.getIsActive());
        }
    }
}
