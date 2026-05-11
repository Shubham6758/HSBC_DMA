package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step2DemandUpdateDTO {
    private String tempId;
    private String displayDemandId;

    // Optional updates
    private Long rrNumber;       // if present -> update RR
    private String jdText;       // if present -> save text as file

    private String filenameHint; // optional hint for text file name
    private Integer fileIndex;   // optional -> points to uploaded file in multipart "files"

}