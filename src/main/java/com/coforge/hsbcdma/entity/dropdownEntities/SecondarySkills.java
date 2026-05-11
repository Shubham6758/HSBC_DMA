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
@Table(name = "secondary_skills")
public class SecondarySkills extends BaseEntity {

    @Column(name = "SECONDARY_SKILLS", nullable = false)
    private String secondarySkills;

    @Transient
    public String getName() {
        return secondarySkills;
    }
}
