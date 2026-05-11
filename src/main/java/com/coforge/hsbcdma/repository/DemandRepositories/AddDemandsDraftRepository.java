package com.coforge.hsbcdma.repository.DemandRepositories;

import com.coforge.hsbcdma.entity.AddDemandDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AddDemandsDraftRepository extends JpaRepository<AddDemandDraft, Long> {
    @Query("""
        SELECT d FROM AddDemandDraft d
        LEFT JOIN FETCH d.rrDrafts
        LEFT JOIN FETCH d.primarySkills
        LEFT JOIN FETCH d.secondarySkills
        LEFT JOIN FETCH d.demandLocations
        WHERE d.id = :id
    """)
    Optional<AddDemandDraft> findFullDraftById(Long id);
}
