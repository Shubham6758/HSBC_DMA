package com.coforge.hsbcdma.entity;

import com.coforge.hsbcdma.dto.RolemgmtDTO.ModuleChildModuleDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "roles")
public class Roles extends BaseEntity{

    @Column(name = "ROLE", nullable = false)
    private String role;

//    @Column(name = "created_by", length = 100)
//    private String createdBy;

    @Column(name = "created_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

//    @Column(name = "updated_by")
//    private String updatedBy;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updateAt;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "module_childmodule", columnDefinition = "json")
    private List<ModuleChildModuleDTO> moduleChildModule;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
//    @PrePersist
//    public void prePersist() {
//        if (this.createdBy == null) {
//            this.createdBy = "system";
//        }
//        if (this.createdAt == null) {
//            this.createdAt = LocalDateTime.now();
//        }
//        if (this.updateAt == null) {
//            this.updateAt = LocalDateTime.now();
//        }
//        if (this.updatedBy == null) {
//            this.updatedBy = "system";
//        }
//    }
}