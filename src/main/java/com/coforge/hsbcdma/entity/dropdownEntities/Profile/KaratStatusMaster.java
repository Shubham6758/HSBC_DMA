package com.coforge.hsbcdma.entity.dropdownEntities.Profile;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "karat_status_master")
@Getter
@Setter
public class KaratStatusMaster extends BaseEntity {

    @Column(name = "name")
    private String name;


    @Transient
    public String getName() {
        return name;
    }
}