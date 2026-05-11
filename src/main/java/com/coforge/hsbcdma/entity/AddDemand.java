package com.coforge.hsbcdma.entity;

import com.coforge.hsbcdma.entity.dropdownEntities.Lob;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "add_demands")
@Getter @Setter @ToString
public class AddDemand extends BaseEntity {

    @Column(name = "flag")
    private Boolean flag;

    @Column(name = "is_subcon_rr")
    private Boolean isSubconRR;

    @Column(name = "karat_flag")
    private Boolean karatFlag;

    @Column(name = "p1_flag_date")
    private LocalDate p1FlagDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hbu_id")
    private Hbu hbu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hbu_spoc_id")
    private HbuSpoc hbuSpoc;
    // demand_id & rr_number now numeric
    @Column(name = "demand_id",nullable = false)
    private Long demandId;

    @Column(name = "rr_number")
    private Long rrNumber;

    // scalar references (same pattern)
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "band_id")
    private Band band;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "priority_id")
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lob_id")
    private Lob lob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_lob_id")
    private SubLob subLob;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "demand_type_id")
    private DemandType demandType;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "demand_timeline_id")
    private DemandTimeLine demandTimeline;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "external_internal_id")
    private ExternalInternal externalInternal;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pod_id")
    private Pod pod;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pmo_spoc_id")
    private PmoSpoc pmoSpoc;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pmo_id")
    private Pmo pmo;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sales_spoc_id")
    private SalesSpoc salesSpoc;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "hiring_manager_id")
    private HiringManager hiringManager;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "delivery_manager_id")
    private DeliveryManager deliveryManager;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "skill_cluster_id")
    private SkillCluster skillCluster;

    @Column(name = "experience")
    private String experience;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "remark", length = 10000)
    private String remark;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_manager_id")
    private ProjectManager projectManager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "add_demand_primary_skills_map",
            joinColumns = @JoinColumn(name = "add_demand_id"),           // references add_demands.id
            inverseJoinColumns = @JoinColumn(name = "primary_skill_id")  // references primary_skills.id
    )
    private Set<PrimarySkills> primarySkills = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "add_demand_secondary_skills_map",
            joinColumns = @JoinColumn(name = "add_demand_id"),
            inverseJoinColumns = @JoinColumn(name = "secondary_skill_id")
    )
    private Set<SecondarySkills> secondarySkills = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "add_demand_locations_map",
            joinColumns = @JoinColumn(name = "add_demand_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> demandLocations = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Column(name = "created_by_name")
    private String createdByName;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "updated_by_user_id")
    private String updatedByUserId;

    @Column(name = "updated_by_name")
    private String updatedByName;


    @Column(name = "demand_received_date", nullable = false)
    private LocalDate demandReceivedDate;

    @OneToMany(mappedBy = "demand", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<ProfileTracker> profileTrackers = new java.util.HashSet<>();
}



