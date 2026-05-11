package com.coforge.hsbcdma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permissions extends BaseEntity{

    @Column(name = "MODULE", nullable = false)
    private String module;
    @Column(name = "PERMISSIONS", nullable = false)
    private String permissions;
}
