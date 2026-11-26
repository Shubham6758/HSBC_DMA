package com.coforge.hsbcdma.entity.ColumnPreference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "user_column_preference",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "page_name"}
                )
        }
)
@Getter
@Setter
public class UserColumnPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "page_name", nullable = false)
    private String pageName;

    @Column(name = "selected_columns", columnDefinition = "TEXT")
    private String selectedColumns;
}
