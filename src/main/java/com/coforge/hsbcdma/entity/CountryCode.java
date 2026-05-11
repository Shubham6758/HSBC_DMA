package com.coforge.hsbcdma.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "country_codes")
public class CountryCode extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "calling_code", nullable = false, unique = true, length = 10)
    private String callingCode; // e.g., "+91"
}