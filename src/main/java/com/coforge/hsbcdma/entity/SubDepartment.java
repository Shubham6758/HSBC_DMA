package com.coforge.hsbcdma.entity;


import jakarta.persistence.*;
import lombok.Data;

// Created by Chetan

@Entity
@Table(name = "subdepartments")
@Data
public class SubDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
