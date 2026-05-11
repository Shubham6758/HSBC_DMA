package com.coforge.hsbcdma.entity.dropdownEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sub_lob")
public class SubLob extends BaseEntity {

    @Column(name = "sub_lob", nullable = false)
    private String subLob;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "lob_id")
    private Lob lob;

    @Transient
    public String getName() {
        return subLob;
    }
}