package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.MonthlyTimesheetRecord;
import com.coforge.hsbcdma.revenuemargin.entity.MonthlyTimesheetEntity;
import com.coforge.hsbcdma.revenuemargin.repository.MonthlyTimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyTimesheetService {

    @Autowired
    private MonthlyTimesheetRepository monthlyTimesheetRepository;
    public void deleteAllRecords() {
        monthlyTimesheetRepository.deleteAll();
    }
    public long getRecordCount() {

        return monthlyTimesheetRepository.count();

    }
    public void saveRecords(
            List<MonthlyTimesheetRecord> records) {

        for (MonthlyTimesheetRecord record : records) {

            MonthlyTimesheetEntity entity =
                    new MonthlyTimesheetEntity();

            entity.setResourceId(record.getResourceId());
            entity.setResourceName(record.getResourceName());
            entity.setTsStatus(record.getTsStatus());
            entity.setTimePeriod(record.getTimePeriod());
            entity.setMon(record.getMon());
            entity.setTue(record.getTue());
            entity.setWed(record.getWed());
            entity.setThu(record.getThu());
            entity.setFri(record.getFri());
            entity.setSat(record.getSat());
            entity.setSun(record.getSun());
            entity.setUnitsConsumed(record.getUnitsConsumed());
            entity.setConsumptionMonth(record.getConsumptionMonth());
            entity.setVendorName(record.getVendorName());
            entity.setContractStartDate(record.getContractStartDate());
            entity.setContractEndDate(record.getContractEndDate());
            entity.setVendorRate(record.getVendorRate());
            entity.setCurrency(record.getCurrency());
            entity.setUnit(record.getUnit());
            entity.setPoNumber(record.getPoNumber());
            entity.setPoLineItemNumber(record.getPoLineItemNumber());
            entity.setBaseLocation(record.getBaseLocation());
            entity.setLegalEntity(record.getLegalEntity());
            entity.setJobCategory(record.getJobCategory());
            entity.setPricingModel(record.getPricingModel());
            entity.setCostCenter(record.getCostCenter());
            entity.setPremises(record.getPremises());
            entity.setOperatingUnit(record.getOperatingUnit());
            entity.setEffectiveStartDate(record.getEffectiveStartDate());
            entity.setEffectiveEndDate(record.getEffectiveEndDate());
            entity.setConsumptionType(record.getConsumptionType());
            entity.setCostActualsLcy(record.getCostActualsLcy());
            entity.setExtendedServiceHourFteConsumed(record.getExtendedServiceHourFteConsumed());
            entity.setExtendedServiceHourLcyAmount(record.getExtendedServiceHourLcyAmount());
            entity.setStaffType(record.getStaffType());
            entity.setEmpClass(record.getEmpClass());
            entity.setBfLevel1(record.getBfLevel1());
            entity.setBfLevel2(record.getBfLevel2());
            entity.setBusinessFunction(record.getBusinessFunction());
            entity.setBusinessSubFunction(record.getBusinessSubFunction());
            entity.setDepartment(record.getDepartment());
            entity.setCountry(record.getCountry());
            entity.setRegion(record.getRegion());
            entity.setBillingMonth(record.getBillingMonth());
            entity.setEquivalentGcb(record.getEquivalentGcb());
            entity.setcToolId(record.getcToolId());
            entity.setcToolFunctionalManager(record.getcToolFunctionalManager());
            entity.setMonth(record.getMonth());

            monthlyTimesheetRepository.save(
                    entity
            );

        }

        System.out.println(
                "Timesheet Records Saved = "
                        + records.size()
        );
    }
    public BigDecimal getActualRate(
            String ctoolId,
            LocalDate consumptionMonth) {

        List<BigDecimal> rates =
                monthlyTimesheetRepository.getActualRate(
                        ctoolId,
                        consumptionMonth);

        if (rates.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return rates.get(0);
    }
    public BigDecimal getActualRevenue(
            String ctoolId,
            LocalDate consumptionMonth) {

        return monthlyTimesheetRepository.getActualRevenue(
                ctoolId,
                consumptionMonth
        );
    }
    public LocalDate getLatestConsumptionMonth() {

        return monthlyTimesheetRepository
                .getLatestConsumptionMonth();
    }
}