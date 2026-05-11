package com.coforge.hsbcdma.entity.dropdownEntities.Profile;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "origins")
@Getter
@Setter
public class Origin extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Transient
    public String getName() {
        return name;
    }
}

