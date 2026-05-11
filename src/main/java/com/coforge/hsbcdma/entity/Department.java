package com.coforge.hsbcdma.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

// Created by Chetan

@Entity
@Table(name = "departments")
@Data
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}

