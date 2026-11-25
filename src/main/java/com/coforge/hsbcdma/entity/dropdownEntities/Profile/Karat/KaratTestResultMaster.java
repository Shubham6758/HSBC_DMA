package com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "karat_test_result_master")
@Getter
@Setter
public class KaratTestResultMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}