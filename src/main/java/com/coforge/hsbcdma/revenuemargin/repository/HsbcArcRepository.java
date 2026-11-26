package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.HsbcArcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface HsbcArcRepository
        extends JpaRepository<
        HsbcArcEntity,
        Long> {
    @Query("""
SELECT COALESCE(SUM(a.ctcInUsd),0) / 12
FROM HsbcArcEntity a
WHERE a.pernr = :empId
AND a.zmonth >= :startDate
AND a.zmonth < :endDate
""")
    BigDecimal getMonthlyCtcInUsd(
            @Param("empId") String empId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("""
SELECT MAX(a.zmonth)
FROM HsbcArcEntity a
WHERE a.pernr = :empId
AND a.ctcInUsd > 0
""")
    LocalDate getLatestArcMonth(
            @Param("empId") String empId
    );

    @Query("""
SELECT COALESCE(SUM(a.ctcInUsd),0) / 12
FROM HsbcArcEntity a
WHERE a.pernr = :empId
AND a.zmonth = :month
""")
    BigDecimal getMonthlyCtcForMonth(
            @Param("empId") String empId,
            @Param("month") LocalDate month
    );
}