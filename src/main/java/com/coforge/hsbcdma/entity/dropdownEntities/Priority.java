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
@Table(name = "priority")
public class Priority extends BaseEntity {

    @Column(name = "priority", nullable = false)
    private String priority;

    @Transient
    public String getName() {
        return priority;
    }
}
