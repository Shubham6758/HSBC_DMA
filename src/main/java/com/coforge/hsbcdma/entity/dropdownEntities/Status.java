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
@Table(name = "status")
public class Status extends BaseEntity {

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Transient
    public String getName() {
        return status;
    }
}