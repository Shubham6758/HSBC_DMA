package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.PrimarySkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrimarySkillsRepository extends JpaRepository<PrimarySkills,Long> {
    List<PrimarySkills> findByIdIn(List<Long> ids);

//    import
    Optional<PrimarySkills> findByPrimarySkillsIgnoreCase(String primarySkills);
}
