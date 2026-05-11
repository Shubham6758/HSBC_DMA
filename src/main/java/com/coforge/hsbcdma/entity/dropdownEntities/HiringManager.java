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
@Table(name = "hiring_manager")
public class HiringManager extends BaseEntity {

    @Column(name = "HIRING_MANAGER", nullable = false)
    private String hiringManager;

    @Transient
    public String getName() {
        return hiringManager;
    }
}
