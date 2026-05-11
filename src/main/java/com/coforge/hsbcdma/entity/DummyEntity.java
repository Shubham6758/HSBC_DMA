package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//This is dummy repository for testing purpose only. Not to be used in application.
//Created by : pratish.b

@Setter
@Getter
@Entity
@Table(name = "DUMMY", uniqueConstraints ={@UniqueConstraint(name="lob_constraint",columnNames = "LOB")})
public class DummyEntity extends BaseEntity{

    @Column(name = "LOB", nullable = false)
    private String lob;

    //@Convert(converter = LocalDateFormatConverter.class)
    @Column(name = "DECISION_DATE", nullable = true)
    private LocalDate decisionDate;

    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_CONTENT_TYPE")
    private String fileContentType;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB") //file size < 16MB
    private byte[] data;
}
