package com.coforge.hsbcdma.dto.PoolRRDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoolRRUpdateRequest {

    private String rr;
    private String skill;
    private Float band;
    private String location;
    private Boolean flag;
}
