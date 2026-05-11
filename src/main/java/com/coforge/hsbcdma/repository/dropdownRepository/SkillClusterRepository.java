package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.SkillCluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillClusterRepository extends JpaRepository<SkillCluster,Long> {
//    import
    Optional<SkillCluster> findBySkillClusterIgnoreCase(String skillCluster);
}
