package com.coforge.hsbcdma.dto.PoolRRDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PoolRRResponse {

    private String rr;
    private Boolean flag;
    private String skill;
    private String band;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}