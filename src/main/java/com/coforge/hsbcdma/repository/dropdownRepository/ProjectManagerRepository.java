package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.ProjectManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectManagerRepository extends JpaRepository<ProjectManager,Long> {
//    import
    Optional<ProjectManager> findByProjectManagerIgnoreCase(String projectManager);
}
