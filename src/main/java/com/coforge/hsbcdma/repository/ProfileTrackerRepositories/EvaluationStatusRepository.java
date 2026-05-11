package com.coforge.hsbcdma.repository.ProfileTrackerRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationStatusRepository extends JpaRepository<EvaluationStatus, Long> { }