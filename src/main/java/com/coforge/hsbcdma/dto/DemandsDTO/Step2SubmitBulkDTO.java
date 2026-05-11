package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;

import java.util.List;

@Data
public class Step2SubmitBulkDTO {
    private List<Step2DemandUpdateDTO> assignments; // demandId, rrNumber, jdText/fileIndex
}
