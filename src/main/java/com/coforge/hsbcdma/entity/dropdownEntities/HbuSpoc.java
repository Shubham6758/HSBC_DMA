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
@Table(name = "hbu_spoc")
public class HbuSpoc extends BaseEntity {

    @Column(name = "hbu_spoc", nullable = false)
    private String hbuSpoc;

    @Transient
    public String getName() {
        return hbuSpoc;
    }
}

