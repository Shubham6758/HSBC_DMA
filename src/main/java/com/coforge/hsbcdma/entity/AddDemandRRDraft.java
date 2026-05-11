package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "add_demand_rr_draft",
        uniqueConstraints = @UniqueConstraint(name = "uk_ad_rr_draft", columnNames = {"draft_id", "demand_id"})
)
@Getter
@Setter
@ToString
public class AddDemandRRDraft extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "draft_id", nullable = false)
    private AddDemandDraft draftId;
//    @Column(name = "draft_id", nullable = false)
//    private Long draftId;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "demand_id")
//    private Long demandId;   // numeric 868, 869, ...

    @Column(name = "rr_number")
    private Long rrNumber;


    @Column(name = "is_subcon_rr")
    private Boolean isSubconRR;


    @Column(name = "file_name", length = 255)
    private String fileName; // e.g., "JD_20260128_221201.txt"
}
