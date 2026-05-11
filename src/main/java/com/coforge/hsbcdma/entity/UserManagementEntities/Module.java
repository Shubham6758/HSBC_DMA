package com.coforge.hsbcdma.entity.UserManagementEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module extends BaseEntity {

    @Column(name = "module", nullable = false, length = 100)
    private String module;
}
