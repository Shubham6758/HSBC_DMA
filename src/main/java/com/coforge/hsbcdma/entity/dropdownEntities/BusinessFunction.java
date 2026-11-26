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
@Table(name = "business_function")
public class BusinessFunction extends BaseEntity {
    @Column(name = "business_function", nullable = false)
    private String businessFunction;

    @Transient
    public String getName() {
        return businessFunction;
    }
}
