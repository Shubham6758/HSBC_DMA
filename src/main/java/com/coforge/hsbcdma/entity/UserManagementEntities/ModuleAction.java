package com.coforge.hsbcdma.entity.UserManagementEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "module_action",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "action_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}
