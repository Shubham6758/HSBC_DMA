package com.coforge.hsbcdma.entity.dropdownEntities.Profile;

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
@Table(name="profile_status")
public class ProfileStatus extends BaseEntity {
    @Column(name = "name",nullable = false)
    private String name;

    @Transient
    public String getName() {
        return name;
    }
}
