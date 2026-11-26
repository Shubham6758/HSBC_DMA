package com.coforge.hsbcdma.entity.ColumnPreference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
        name = "user_filter_preferences",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "filter_name"}
        )
)
public class UserFilterPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "filter_name", nullable = false)
    private String filterName;

    @Column(name = "filter_value", columnDefinition = "TEXT")
    private String filterValue;
}
