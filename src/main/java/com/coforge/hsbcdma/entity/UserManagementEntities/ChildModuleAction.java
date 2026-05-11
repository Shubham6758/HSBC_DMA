package com.coforge.hsbcdma.entity.UserManagementEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "childmodule_action",
        uniqueConstraints = @UniqueConstraint(columnNames = {"childmodule_id", "action_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildModuleAction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "childmodule_id", nullable = false)
    private ChildModule childModule;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}
