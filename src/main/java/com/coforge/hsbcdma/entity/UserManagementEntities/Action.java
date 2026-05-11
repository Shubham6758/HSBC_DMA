package com.coforge.hsbcdma.entity.UserManagementEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        name = "actions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_child_action",
                columnNames = { "child_id", "name" }
        )
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Action extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildModule childModule;


    @Column(name = "name", nullable = false, length = 50)
    private String action;
}
