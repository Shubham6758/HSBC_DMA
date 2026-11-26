package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.HsbcArcRecord;
import com.coforge.hsbcdma.revenuemargin.entity.HsbcArcEntity;
import com.coforge.hsbcdma.revenuemargin.repository.HsbcArcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HsbcArcService {

    @Autowired
    private HsbcArcRepository hsbcArcRepository;
    public void deleteAllRecords() {
        hsbcArcRepository.deleteAll();
    }

    public long getRecordCount() {

        return hsbcArcRepository.count();

    }
    public void saveRecords(
            List<HsbcArcRecord> records) {

        for (HsbcArcRecord record : records) {

            HsbcArcEntity entity =
                    new HsbcArcEntity();

            entity.setPernr(
                    record.getPernr()
            );

            entity.setZmonth(
                    record.getZmonth()
            );

            entity.setCostToCo(record.getCostToCo());
            entity.setCtcInUsd(record.getCtcInUsd());
            entity.setRecType(record.getRecType());
            entity.setCountry(record.getCountry());
            entity.setCountryKey(record.getCountryKey());
            entity.setCustomerName(record.getCustomerName());
            entity.setCustomerGrp(record.getCustomerGrp());
            entity.setIbu(record.getIbu());
            entity.setSubIbu(record.getSubIbu());
            entity.setAnsvh(record.getAnsvh());
            entity.setBand(record.getBand());
            entity.setConversionRate(record.getConversionRate());
            entity.setCurrency(record.getCurrency());
            entity.setLocation(record.getLocation());
            entity.setDeliveryL1(record.getDeliveryL1());
            entity.setDeliveryL2(record.getDeliveryL2());
            entity.setEmpHorizontal(record.getEmpHorizontal());
            entity.setProjectType(record.getProjectType());
            entity.setBukrs(record.getBukrs());
            entity.setEmpGroupPrct(record.getEmpGroupPrct());
            entity.setOrgeh(record.getOrgeh());
            entity.setPosid(record.getPosid());
            entity.setIsActive(record.getIsActive());
            entity.setMonthYear(record.getMonthYear());
            entity.setCreatedOn(record.getCreatedOn());
            entity.setModifiedOn(record.getModifiedOn());
            entity.setDeliveryL0(record.getDeliveryL0());
            entity.setWbsElement(record.getWbsElement());
            hsbcArcRepository.save(
                    entity
            );

        }

        System.out.println(
                "ARC Records Saved = "
                        + records.size()
        );

    }
    public BigDecimal getMonthlyCtcInUsd(
            String empId,
            LocalDate startDate,
            LocalDate endDate) {

        return hsbcArcRepository.getMonthlyCtcInUsd(
                empId,
                startDate,
                endDate
        );
    }
    public BigDecimal getLatestAvailableMonthlyCtc(
            String empId) {

        LocalDate latestMonth =
                hsbcArcRepository.getLatestArcMonth(
                        empId
                );

        if (latestMonth == null) {
            return BigDecimal.ZERO;
        }

        return hsbcArcRepository.getMonthlyCtcForMonth(
                empId,
                latestMonth
        );
    }
}