//package com.coforge.hsbcdma.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "demand_history")
//public class DemandHistory extends BaseEntity{
//
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "demand_id", nullable = false)
//    private Demand demand;
//
//
//    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
//    private LocalDateTime updateAt;
//
//    @Column(name = "changed_by_user_id")
//    private String changedByUserId;
//
//    @Column(name = "changed_by_name")
//    private String changedByName;
//
//
//    @Column(name = "payload", nullable = false, columnDefinition = "json")
//    private String Payload;
//}


