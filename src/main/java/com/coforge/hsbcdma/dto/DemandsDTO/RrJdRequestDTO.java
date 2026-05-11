package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RrJdRequestDTO {
    private Long rrNumber;

    // Choose one:
    private String fileName;     // must match MultipartFile.getOriginalFilename()
    private String jdText;       // store as text file if provided

    private String filenameHint; // optional hint for text file naming

    // New
    private Boolean isSubconRR;
}

