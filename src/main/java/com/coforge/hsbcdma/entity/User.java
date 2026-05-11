package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing a user in the system.
 * It stores user identity, authentication credentials,
 * and authorization-related information.
 *
 * This entity is mapped to the underlying user database table
 * and is used for authentication and access control.
 *
 * @author Vandana Pal
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @NotBlank(message = "UserId can not be blank")
    private String userId; //username

    private String password;

    private String tempPassword;
    @NotNull
    @Column(name = "phone_number")
    private Long phoneNumber;
    @NotBlank
    @Email
    private String emailId;
//        @NotBlank(message = "Role can not be blank.")
//    private String role;
//    private String permissions;
    private boolean firstLogin = false;

//
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryCode country;


    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdepartment_id")
    private SubDepartment subdepartment;


    // ✅ SINGLE LOGIN FIELDS
    @Column(length = 1200)
    private String activeToken;

    private LocalDateTime tokenExpiresAt;


}