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
@Table(name = "PMO_SPOC")
public class PmoSpoc extends BaseEntity {

    @Column(name = "PMO_SPOC", nullable = true)
    private String pmoSpoc ;

    @Transient
    public String getName() {
        return pmoSpoc;
    }
}
