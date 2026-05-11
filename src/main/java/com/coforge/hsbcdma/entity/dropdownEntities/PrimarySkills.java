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
@Table(name = "primary_skills")
public class PrimarySkills extends BaseEntity {

    @Column(name = "PRIMARY_SKILLS", nullable = false)
    private String primarySkills;

    @Transient
    public String getName() {
        return primarySkills;
    }
}
