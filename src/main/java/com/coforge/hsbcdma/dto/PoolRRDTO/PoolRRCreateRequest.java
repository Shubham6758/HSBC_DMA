package com.coforge.hsbcdma.dto.PoolRRDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoolRRCreateRequest {

    @NotBlank(message = "RR is mandatory")
    private String rr;

    private String skill;
    private Float band;
    private String location;
    private Boolean flag;
}

