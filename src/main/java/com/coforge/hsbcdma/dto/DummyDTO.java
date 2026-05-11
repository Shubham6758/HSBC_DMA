package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.util.LocalDateFormatConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

//This is dummy DTO for testing purpose only. Not to be used in application.
//Created by : pratish.b
@Getter
@Setter
public class DummyDTO {
    @NotNull
    private String lob;
    @NotNull
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate decisionDate;
}
