package com.coforge.hsbcdma.entity.dropdownEntities.Profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "source_master")
@Getter
@Setter
public class SourceMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
