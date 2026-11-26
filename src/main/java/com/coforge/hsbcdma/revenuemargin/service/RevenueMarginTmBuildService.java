package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import com.coforge.hsbcdma.revenuemargin.entity.VmDumpEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueMarginTmBuildService {

    @Autowired
    private RevenueMarginTmRepository revenueMarginTmRepository;
    @Autowired
    private HsbcArcService hsbcArcService;
    @Autowired
    private RevenueMarginEnrichmentService enrichmentService;
    @Autowired
    private MonthlyTimesheetService monthlyTimesheetService;

    private BigDecimal getActualRate(RevenueMarginTmEntity record,LocalDate month) {
        return monthlyTimesheetService.getActualRate(record.getCtool(),month);}

    private BigDecimal getActualRevenue(RevenueMarginTmEntity record, LocalDate month) {
        return monthlyTimesheetService.getActualRevenue(record.getCtool(),month);}
    private LocalDate getLatestActualMonth() {return LocalDate.of(2026, 9, 1);}
    private List<LocalDate> getActualMonths() {
        List<LocalDate> months = new ArrayList<>();
        LocalDate month = LocalDate.of(2026, 4, 1);
        while (!month.isAfter(getLatestActualMonth())) {
            months.add(month);
            month = month.plusMonths(1);
        }
        return months;
    }
    private BigDecimal getActualMonthlyCtc(RevenueMarginTmEntity record,LocalDate month) {
        BigDecimal ctc = hsbcArcService.getMonthlyCtcInUsd(record.getEmpId(),month,month.plusMonths(1));
        if (ctc.compareTo(BigDecimal.ZERO) > 0) {return ctc;}
        return hsbcArcService.getLatestAvailableMonthlyCtc(record.getEmpId());
    }
    private BigDecimal getActualMarginPercent(RevenueMarginTmEntity record,LocalDate month) {
        BigDecimal revenue = getActualRevenue(record,month);
        BigDecimal ctc = getActualMonthlyCtc(record,month);
        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) == 0) {return BigDecimal.ZERO;}
        return revenue.subtract(ctc).multiply(new BigDecimal("100")).divide(revenue,2,RoundingMode.HALF_UP);
    }

    private BigDecimal getForecastWorkingDays(RevenueMarginTmEntity record,LocalDate month) {
        if ("Non-Active".equalsIgnoreCase(record.getResourceStatus())) {return BigDecimal.ZERO;}
        LocalDate startDate = record.getEffectiveStartDate();
        LocalDate endDate = record.getEffectiveEndDate();
        LocalDate monthStart = month.withDayOfMonth(1);
        LocalDate monthEnd = month.withDayOfMonth(month.lengthOfMonth());
        if (startDate == null || endDate == null) {return BigDecimal.ZERO;}
        if (startDate.isAfter(monthEnd)) {return BigDecimal.ZERO;}
        if (endDate.isBefore(monthStart)) {return BigDecimal.ZERO;}

        LocalDate actualStart =
                startDate.isAfter(monthStart)
                        ? startDate
                        : monthStart;

        LocalDate actualEnd =
                endDate.isBefore(monthEnd)
                        ? endDate
                        : monthEnd;

        long workingDays = 0;

        for (LocalDate date = actualStart;
             !date.isAfter(actualEnd);
             date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY
                    && date.getDayOfWeek() != DayOfWeek.SUNDAY) {workingDays++;}
        }
        return BigDecimal.valueOf(workingDays);
    }

    private BigDecimal getForecastLeaves(BigDecimal workingDays) {return workingDays.multiply(BigDecimal.valueOf(0.10));}

    private BigDecimal getForecastRate(RevenueMarginTmEntity record) {return record.getEffectiveRate();}
    private BigDecimal getForecastRevenue(BigDecimal rate,BigDecimal workingDays,BigDecimal leaves) {
        return rate.multiply(workingDays.subtract(leaves));
    }
    private BigDecimal getForecastMarginPercent(BigDecimal revenue,BigDecimal ctc) {
        if (revenue.compareTo(BigDecimal.ZERO) == 0) {return BigDecimal.ZERO;}
        return revenue.subtract(ctc).multiply(BigDecimal.valueOf(100)).divide(revenue,2,RoundingMode.HALF_UP);
    }
    private boolean isActualMonth(LocalDate month,LocalDate latestConsumptionMonth) {
        return !month.isAfter(latestConsumptionMonth);}
    private boolean isForecastMonth(LocalDate month,LocalDate latestConsumptionMonth) {
        return month.isAfter(latestConsumptionMonth);}
    public void enrichData() {
        List<RevenueMarginTmEntity> records = revenueMarginTmRepository.findAll();
        LocalDate latestConsumptionMonth = monthlyTimesheetService.getLatestConsumptionMonth();
        boolean sep26Actual = isActualMonth(LocalDate.of(2026, 9, 1),latestConsumptionMonth);
        sep26Actual = !LocalDate.of(2026, 9, 1).isAfter(latestConsumptionMonth);

        LocalDate nextForecastMonth = latestConsumptionMonth.plusMonths(1);
        for (RevenueMarginTmEntity record : records) {
            //record.setCoforgeBand(enrichmentService.getCoforgeBandFromResourceTracker(record.getPsid()));
            //record.setFteSubCon(enrichmentService.getFteSubConFromResourceTracker(record.getPsid(), record.getResourceStatus()));
            String coforgeBand = enrichmentService.getCoforgeBandFromResourceTracker(record.getPsid());
            record.setCoforgeBand(coforgeBand);
            record.setFteSubCon("0".equals(coforgeBand) ? "Seconded/Outsource" : "Regular Staff Member");
            record.setPricingModel(enrichmentService.getPricingModel(record.getPsid()));
            record.setEffectiveStartDate(enrichmentService.getEffectiveStartDate(record.getPsid()));
            record.setEffectiveEndDate(enrichmentService.getEffectiveEndDate(record.getPsid()));
            record.setResourceStatus(enrichmentService.getResourceStatus(record.getPsid()));
            record.setCountry(enrichmentService.getCountry(record.getPsid()));
            record.setGbGi(enrichmentService.getGbGi(record.getCtool()));
            VmDumpEntity vmDump = enrichmentService.getVmDumpByCtool(record.getCtool());
            record.setBfLevel3(vmDump == null ? "" : vmDump.getBfLevel3());
            record.setBfLevel4(vmDump == null ? "" : vmDump.getBfLevel4());


            record.setJobCategoryGcbLevel(enrichmentService.getJobCategoryGcbLevel(record.getPsid()));
            record.setCtoolRate(enrichmentService.getCToolRate(record.getPsid()));

            record.setM4Rate(enrichmentService.getM4Rate(record.getJobCategoryGcbLevel()));

            record.setEffectiveRate(
                    enrichmentService.getEffectiveRate(
                            record.getPricingModel(),
                            record.getName(),
                            record.getGbGi(),
                            record.getCtoolRate(),
                            record.getM4Rate()));
            LocalDate apr2026 = LocalDate.of(2026, 4, 1);
            record.setApr26Rate(getActualRate(record,apr2026));
            record.setApr26Revenue(getActualRevenue(record,apr2026));
            record.setApr26Ctc(getActualMonthlyCtc(record,apr2026));
            record.setApr26Margin(getActualMarginPercent(record,apr2026));
            LocalDate may2026 = LocalDate.of(2026, 5, 1);
            record.setMay26Rate(getActualRate(record, may2026));
            record.setMay26Revenue(getActualRevenue(record, may2026));
            record.setMay26Ctc(getActualMonthlyCtc(record, may2026));
            record.setMay26Margin(getActualMarginPercent(record, may2026));
            LocalDate jun2026 = LocalDate.of(2026, 6, 1);
            record.setJun26Rate(getActualRate(record, jun2026));
            record.setJun26Revenue(getActualRevenue(record, jun2026));
            record.setJun26Ctc(getActualMonthlyCtc(record, jun2026));
            record.setJun26Margin(getActualMarginPercent(record, jun2026));
            record.setQ1Rev(record.getApr26Revenue().add(record.getMay26Revenue()).add(record.getJun26Revenue()));
            record.setQ1Cost(record.getApr26Ctc().add(record.getMay26Ctc()).add(record.getJun26Ctc()));
            if (record.getQ1Rev().compareTo(BigDecimal.ZERO) == 0) {
                record.setQ1Margin(BigDecimal.ZERO);
            } else {
                record.setQ1Margin(record.getQ1Rev().subtract(record.getQ1Cost()).multiply(BigDecimal.valueOf(100))
                                .divide(record.getQ1Rev(),2,RoundingMode.HALF_UP));
            }
            LocalDate jul2026 = LocalDate.of(2026, 7, 1);

            record.setJul26Rate(getActualRate(record, jul2026));
            record.setJul26Revenue(getActualRevenue(record, jul2026));
            record.setJul26Ctc(getActualMonthlyCtc(record, jul2026));
            record.setJul26Margin(getActualMarginPercent(record, jul2026));

            LocalDate aug2026 = LocalDate.of(2026, 8, 1);

            record.setAug26Rate(getActualRate(record, aug2026));
            record.setAug26Revenue(getActualRevenue(record, aug2026));
            record.setAug26Ctc(getActualMonthlyCtc(record, aug2026));
            record.setAug26Margin(getActualMarginPercent(record, aug2026));

            LocalDate sep2026 = LocalDate.of(2026, 9, 1);
            //record.setSep26WorkingDays(getForecastWorkingDays(record, sep2026));
            //record.setSep26HolidaysLeaves(getForecastLeaves(record.getSep26WorkingDays()));
            record.setSep26Rate(getActualRate(record, sep2026));
            record.setSep26Revenue(getActualRevenue(record, sep2026));
            record.setSep26Ctc(getActualMonthlyCtc(record, sep2026));
            record.setSep26Margin(getActualMarginPercent(record, sep2026));
            LocalDate oct2026 = LocalDate.of(2026, 10, 1);
            //record.setOct26WorkingDays(getForecastWorkingDays(record, oct2026));
            //record.setOct26HolidaysLeaves(getForecastLeaves(record.getOct26WorkingDays()));
            record.setOct26Rate(getActualRate(record, oct2026));
            record.setOct26Revenue(getActualRevenue(record, oct2026));
            record.setOct26Ctc(getActualMonthlyCtc(record,oct2026));
            record.setOct26Margin(getActualMarginPercent(record, oct2026));

            LocalDate nov2026 = LocalDate.of(2026, 11, 1);
            record.setNov26WorkingDays(getForecastWorkingDays(record, nov2026));
            record.setNov26HolidaysLeaves(getForecastLeaves(record.getNov26WorkingDays()));
            record.setNov26Rate(getForecastRate(record));
            record.setNov26Revenue(getForecastRevenue(record.getNov26Rate(),record.getNov26WorkingDays(),record.getNov26HolidaysLeaves()));
            record.setNov26Ctc(getActualMonthlyCtc(record,nov2026));
            record.setNov26Margin(getForecastMarginPercent(record.getNov26Revenue(),record.getNov26Ctc()));
            LocalDate dec2026 = LocalDate.of(2026, 12, 1);

            record.setDec26WorkingDays(getForecastWorkingDays(record, dec2026));
            record.setDec26HolidaysLeaves(getForecastLeaves(record.getDec26WorkingDays()));
            record.setDec26Rate(getForecastRate(record));
            record.setDec26Revenue(getForecastRevenue(record.getDec26Rate(),record.getDec26WorkingDays(),record.getDec26HolidaysLeaves()));
            record.setDec26Ctc(getActualMonthlyCtc(record,dec2026));

            record.setDec26Margin(getForecastMarginPercent(record.getDec26Revenue(),record.getDec26Ctc()));
            LocalDate jan2027 = LocalDate.of(2027, 1, 1);
            record.setJan27WorkingDays(getForecastWorkingDays(record, jan2027));
            record.setJan27HolidaysLeaves(getForecastLeaves(record.getJan27WorkingDays()));
            record.setJan27Rate(getForecastRate(record));
            record.setJan27Revenue(getForecastRevenue(record.getJan27Rate(),record.getJan27WorkingDays(),record.getJan27HolidaysLeaves()));
            record.setJan27Ctc(getActualMonthlyCtc(record,jan2027));
            record.setJan27Margin(getForecastMarginPercent(record.getJan27Revenue(),record.getJan27Ctc()));
            LocalDate feb2027 = LocalDate.of(2027, 2, 1);
            record.setFeb27WorkingDays(getForecastWorkingDays(record, feb2027));
            record.setFeb27HolidaysLeaves(getForecastLeaves(record.getFeb27WorkingDays()));
            record.setFeb27Rate(getForecastRate(record));
            record.setFeb27Revenue(getForecastRevenue(record.getFeb27Rate(),record.getFeb27WorkingDays(),record.getFeb27HolidaysLeaves()));
            record.setFeb27Ctc(getActualMonthlyCtc(record,feb2027));
            record.setFeb27Margin(getForecastMarginPercent(record.getFeb27Revenue(),record.getFeb27Ctc()));
            LocalDate mar2027 = LocalDate.of(2027, 3, 1);
            record.setMar27WorkingDays(getForecastWorkingDays(record, mar2027));
            record.setMar27HolidaysLeaves(getForecastLeaves(record.getMar27WorkingDays()));
            record.setMar27Rate(getForecastRate(record));
            record.setMar27Revenue(getForecastRevenue(record.getMar27Rate(),record.getMar27WorkingDays(),record.getMar27HolidaysLeaves()));
            record.setMar27Ctc(getActualMonthlyCtc(record,mar2027));
            record.setMar27Margin(getForecastMarginPercent(record.getMar27Revenue(),record.getMar27Ctc()));
            record.setQ2Rev(record.getJul26Revenue().add(record.getAug26Revenue()).add(record.getSep26Revenue()));
            record.setQ2Cost(record.getJul26Ctc().add(record.getAug26Ctc()).add(record.getSep26Ctc()));
            record.setQ2Margin(getForecastMarginPercent(record.getQ2Rev(),record.getQ2Cost()));
            record.setQ3Rev(record.getOct26Revenue().add(record.getNov26Revenue()).add(record.getDec26Revenue()));
            record.setQ3Cost(record.getOct26Ctc().add(record.getNov26Ctc()).add(record.getDec26Ctc()));
            record.setQ3Margin(getForecastMarginPercent(record.getQ3Rev(),record.getQ3Cost()));
            record.setQ4Rev(record.getJan27Revenue().add(record.getFeb27Revenue()).add(record.getMar27Revenue()));
            record.setQ4Cost(record.getJan27Ctc().add(record.getFeb27Ctc()).add(record.getMar27Ctc()));
            record.setQ4Margin(getForecastMarginPercent(record.getQ4Rev(),record.getQ4Cost()));
            record.setFy202627Rev(record.getQ1Rev().add(record.getQ2Rev()).add(record.getQ3Rev()).add(record.getQ4Rev()));
            record.setFy202627Cost(record.getQ1Cost().add(record.getQ2Cost()).add(record.getQ3Cost()).add(record.getQ4Cost()));
            record.setFy202627Margin(getForecastMarginPercent(record.getFy202627Rev(),record.getFy202627Cost()));
            revenueMarginTmRepository.save(record);
        }
    }
    private void testActualCalculations(RevenueMarginTmEntity record) {
        LocalDate apr2026 = LocalDate.of(2026, 4, 1);
    }
}