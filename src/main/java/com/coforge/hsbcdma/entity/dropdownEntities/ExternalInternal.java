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
@Table(name = "EXTERNAL_INTERNAL")
public class ExternalInternal extends BaseEntity {

    @Column(name = "EXTERNAL_INTERNAL", nullable = true)
    private String externalInternal;

    @Transient
    public String getName() {
        return externalInternal;
    }
}
