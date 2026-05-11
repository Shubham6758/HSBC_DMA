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
@Table(name = "pmo")
public class Pmo extends BaseEntity {

    @Column(name = "PMO", nullable = false)
    private String pmo;

    @Transient
    public String getName() {
        return pmo;
    }
}
