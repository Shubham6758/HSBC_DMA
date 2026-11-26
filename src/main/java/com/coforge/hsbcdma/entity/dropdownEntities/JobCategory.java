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
@Table(name = "job_category")
public class JobCategory extends BaseEntity {
    @Column(name = "job_category", nullable = false)
    private String jobCategory;

    @Transient
    public String getName() {
        return jobCategory;
    }
}
