package com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prep_status_master")
@Getter
@Setter
public class PrepStatusMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
