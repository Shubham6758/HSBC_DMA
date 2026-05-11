package com.coforge.hsbcdma.entity.dropdownEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "project_manager")
public class ProjectManager extends BaseEntity {

    @Column(name = "project_manager", nullable = false)
    private String projectManager;


    @Transient
    public String getName() {
        return projectManager;
    }
}
 