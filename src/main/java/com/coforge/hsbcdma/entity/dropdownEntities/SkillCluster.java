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
@Table(name = "skill_cluster")
public class SkillCluster extends BaseEntity {

    @Column(name = "SKILL_CLUSTER", nullable = false)
    private String skillCluster;

    @Transient
    public String getName() {
        return skillCluster;
    }
}
