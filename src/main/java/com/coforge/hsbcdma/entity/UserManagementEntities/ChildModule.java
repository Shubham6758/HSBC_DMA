package com.coforge.hsbcdma.entity.UserManagementEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "childmodule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildModule extends BaseEntity{

    @Column(name = "childmodule", nullable = false, length = 100)
    private String childModule;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

//    @Column(name = "is_action")
//    @ColumnDefault("false")
//    private boolean action;



    @OneToMany(
            mappedBy = "childModule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Action> actions = new ArrayList<>();

}
