package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RevenueMarginTmRepository
        extends JpaRepository<RevenueMarginTmEntity, Long> {

    long count();

    long countByResourceStatus(String resourceStatus);

    long countByCountry(String country);

    long countByPricingModel(String pricingModel);

    @Query("""
            SELECT COALESCE(SUM(r.fy202627Rev),0)
            FROM RevenueMarginTmEntity r
            """)
    BigDecimal getTotalFyRevenue();

    @Query("""
            SELECT COALESCE(SUM(r.fy202627Cost),0)
            FROM RevenueMarginTmEntity r
            """)
    BigDecimal getTotalFyCost();

    @Query("""
            SELECT COALESCE(SUM(r.fy202627Margin),0)
            FROM RevenueMarginTmEntity r
            """)
    BigDecimal getTotalFyMargin();

    @Query("""
            SELECT COALESCE(AVG(r.effectiveRate),0)
            FROM RevenueMarginTmEntity r
            WHERE r.effectiveRate IS NOT NULL
            """)
    BigDecimal getAverageEffectiveRate();
    @Query("""
SELECT COALESCE(SUM(r.apr26Revenue),0)
FROM RevenueMarginTmEntity r
""")
    BigDecimal getAprRevenue();

    @Query("""
SELECT COALESCE(SUM(r.may26Revenue),0)
FROM RevenueMarginTmEntity r
""")
    BigDecimal getMayRevenue();

    @Query("""
SELECT COALESCE(SUM(r.jun26Revenue),0)
FROM RevenueMarginTmEntity r
""")
    BigDecimal getJunRevenue();
    List<RevenueMarginTmEntity> findTop10ByOrderByEffectiveRateDesc();
    long countByFy202627MarginLessThan(BigDecimal margin);

    @Procedure(procedureName = "sp_tm_add_new_resources")
    void addNewResources();

    List<RevenueMarginTmEntity> findAll();
}