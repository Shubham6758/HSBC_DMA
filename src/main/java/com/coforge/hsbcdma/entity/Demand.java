package com.coforge.hsbcdma.entity;

import com.coforge.hsbcdma.entity.enums.DemandStatus;
import com.coforge.hsbcdma.entity.enums.Externality;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Demand entity representing a demand against which profiles are uploaded.
 */
@Getter
@Setter
@Entity
@Table(name = "demands")
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String demandBusinessId; // Business Demand ID

    @Column(name = "rr")
    private String rr;

    private String lob;
    private String hiringManager;
    private String skillCluster;
    private String primarySkill;
    private String secondarySkill;

    @Enumerated(EnumType.STRING)
    private Externality externality;

    @Enumerated(EnumType.STRING)
    private DemandStatus status = DemandStatus.OPEN;

    private LocalDateTime dateProfileShared = LocalDateTime.now(); // auto-populate when a profile is shared

    //@OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<ProfileTracker> profiles = new ArrayList<>();
}
