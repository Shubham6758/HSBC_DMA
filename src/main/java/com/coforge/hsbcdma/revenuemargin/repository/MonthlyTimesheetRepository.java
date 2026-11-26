package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.MonthlyTimesheetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyTimesheetRepository
        extends JpaRepository<
        MonthlyTimesheetEntity,
        Long> {
            @Query("""
        SELECT COALESCE(SUM(m.costActualsLcy),0)
        FROM MonthlyTimesheetEntity m
        WHERE m.cToolId = :ctoolId
        AND m.consumptionMonth = :consumptionMonth
        """)
            BigDecimal getActualRevenue(
                    @Param("ctoolId") String ctoolId,
                    @Param("consumptionMonth") LocalDate consumptionMonth
            );
    @Query("""
SELECT m.vendorRate
FROM MonthlyTimesheetEntity m
WHERE m.cToolId = :ctoolId
AND m.consumptionMonth = :consumptionMonth
""")
    List<BigDecimal> getActualRate(
            @Param("ctoolId") String ctoolId,
            @Param("consumptionMonth") LocalDate consumptionMonth
    );
    @Query("""
SELECT MAX(m.consumptionMonth)
FROM MonthlyTimesheetEntity m
""")
    LocalDate getLatestConsumptionMonth();
}