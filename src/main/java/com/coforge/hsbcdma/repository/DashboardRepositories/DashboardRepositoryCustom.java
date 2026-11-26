package com.coforge.hsbcdma.repository.DashboardRepositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> executeDynamicQuery(
            String sql,
            Map<String, Object> params) {

        Query query = em.createNativeQuery(sql);

        if (params != null) {
            params.forEach(query::setParameter);
        }

        return query.getResultList();
    }
}