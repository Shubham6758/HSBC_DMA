package com.coforge.hsbcdma.entity;


import jakarta.persistence.*;
import lombok.Data;

// Created by Chetan
@Entity
@Table(name = "locations")
@Data
public class Location extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type = LocationType.ONSHORE;

    @Transient
    public String getName() {
        return name;
    }
    public enum LocationType {
        ONSHORE,
        OFFSHORE
    }
}
