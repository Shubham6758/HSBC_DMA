package com.coforge.hsbcdma.entity.dropdownEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "lob")
public class Lob extends BaseEntity {

    @Column(name = "LOB", nullable = false)
    private String lob;

    @Column(name = "CURRENT_DEMAND_ID_SEQUENCE", nullable = false)
    private Integer currentDemandIdSequence;

    @Transient
    public String getName() {
        return lob;
    }
}
