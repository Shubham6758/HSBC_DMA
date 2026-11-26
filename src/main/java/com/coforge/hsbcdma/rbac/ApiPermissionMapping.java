package com.coforge.hsbcdma.rbac;

import com.coforge.hsbcdma.entity.BaseEntity;
import com.coforge.hsbcdma.entity.UserManagementEntities.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "api_permission_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_api_permission_method_path",
                        columnNames = {
                                "http_method",
                                "path_pattern"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPermissionMapping extends BaseEntity {

    @Column(
            name = "http_method",
            nullable = false,
            length = 10
    )
    private String httpMethod;

    @Column(
            name = "path_pattern",
            nullable = false,
            length = 255
    )
    private String pathPattern;

    /*
     * Can be null when permissionRequired = false.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "action_id",
            foreignKey = @ForeignKey(
                    name = "fk_api_permission_action"
            )
    )
    private Action action;

    /*
     * true  = action permission must be checked
     * false = accessible to every authenticated user
     */
    @Column(
            name = "permission_required",
            nullable = false
    )
    private boolean permissionRequired = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}