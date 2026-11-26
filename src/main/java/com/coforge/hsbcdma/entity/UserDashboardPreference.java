package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "user_dashboard_preference",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "dashboard_name"}
                )
        }
)
@Getter
@Setter
public class UserDashboardPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "dashboard_name", nullable = false)
    private String dashboardName;

    @Column(name = "hbu_ids", columnDefinition = "TEXT")
    private String hbuIds;

    @Column(name = "hm_ids", columnDefinition = "TEXT")
    private String hmIds;

    @Column(name = "skill_ids", columnDefinition = "TEXT")
    private String skillIds;

    @Column(name = "lob_ids", columnDefinition = "TEXT")
    private String lobIds;

    @Column(columnDefinition = "TEXT")
    private String priorityIds;
}
