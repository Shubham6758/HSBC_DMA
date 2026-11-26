package com.coforge.hsbcdma.dto.ReportDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandAgingDTO {

    private Long ageing0To15;
    private Long ageing16To30;
    private Long ageing31To60;
    private Long ageing60Plus;
}
