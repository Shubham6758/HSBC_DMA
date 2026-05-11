package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.validate.Stages;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandRRDTO {
    @NotBlank(groups = Stages.Stage2.class)
    private Long demandId;
    //@NotNull(groups = Stages.Stage2.class)
    @Digits(integer = 10, fraction = 0, groups = Stages.Stage2.class)
    private Long rrNumber;

    private String file_name;
}

