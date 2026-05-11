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
@Table(name = "pod")
public class Pod extends BaseEntity {

    @Column(name = "pod", nullable = false)
    private String pod;

    @Transient
    public String getName() {
        return pod;
    }
}
