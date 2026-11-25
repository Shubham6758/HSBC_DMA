package com.coforge.hsbcdma.repository.ReportRepositories;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    // ✅ generic return type (IMPORTANT FIX)
    public List<?> executeDynamicQuery(String sql) {
        return em.createNativeQuery(sql).getResultList();
    }
}


