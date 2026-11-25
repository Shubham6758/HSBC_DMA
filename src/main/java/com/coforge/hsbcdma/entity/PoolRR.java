package com.coforge.hsbcdma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pool_rr")
@Getter
@Setter
public class PoolRR extends BaseEntity {

    @Column(name = "rr", nullable = false, unique = true)
    private String rr;

    @Column(name = "flag") // nullable by default
    private Boolean flag;


    @Column(name = "skill")
    private String skill;

    @Column(name = "band")
    private Float band;

    @Column(name = "location")
    private String location;

    // Optional audit fields (recommended)
    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Column(name = "updated_by_user_id")
    private String updatedByUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
