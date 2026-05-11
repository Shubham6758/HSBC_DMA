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
@Table(name = "demand_type")
public class DemandType extends BaseEntity {

    @Column(name = "DEMAND_TYPE", nullable = false)
    private String demandType;

    @Transient
    public String getName() {
        return demandType;
    }
}
