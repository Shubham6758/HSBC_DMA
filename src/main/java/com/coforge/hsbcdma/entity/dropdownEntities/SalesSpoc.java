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
@Table(name = "sales_spoc")
public class SalesSpoc extends BaseEntity {

    @Column(name = "SALES_SPOC", nullable = false)
    private String salesSpoc;

    @Transient
    public String getName() {
        return salesSpoc;
    }
}
