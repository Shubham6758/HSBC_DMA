package com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coderpad_result_master")
@Getter
@Setter
public class CoderpadResultMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Pass / Fail / Pending
}
