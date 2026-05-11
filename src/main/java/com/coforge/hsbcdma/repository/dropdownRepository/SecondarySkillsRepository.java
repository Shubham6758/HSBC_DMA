package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.SecondarySkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SecondarySkillsRepository extends JpaRepository<SecondarySkills,Long> {
    List<SecondarySkills> findByIdIn(List<Long> ids);
//import
    Optional<SecondarySkills> findBySecondarySkillsIgnoreCase(String secondarySkills);

}
