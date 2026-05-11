package com.coforge.hsbcdma.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "add_demand_history")
@Getter
@Setter
public class AddDemandHistory extends BaseEntity{

    @Column(name = "demand_id", nullable = false)
    private Long demandId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(name = "changed_by_user_id")
    private String changedByUserId;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime changedAt;

//    @Column(name = "summary", length = 500)
//    private String summary;
//
//    // Store as JSON strings; Hibernate will treat as TEXT unless using a JSON type mapping
//    @Lob
//    @Column(name = "old_data", columnDefinition = "JSON")
//    private String oldData;
//
//    @Lob
//    @Column(name = "new_data", columnDefinition = "JSON")
//    private String newData;

    @Lob
    @Column(name = "diff", columnDefinition = "JSON")
    private String diff;

    public enum Action { CREATE, UPDATE }
}

