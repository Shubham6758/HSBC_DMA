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
@Table(name = "contract_type")
public class ContractType extends BaseEntity {
    @Column(name = "contract_type", nullable = false)
    private String contractType;

    @Transient
    public String getName() {
        return contractType;
    }
}
