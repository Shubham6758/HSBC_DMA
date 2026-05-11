package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

// Optional typed JD text (if not uploading a file)
@Getter
@Setter
public class Step2JDTextDTO {
    private Long demandId;
    private Long draftId;
    private String jdText; // will be saved as .txt
    private String filenameHint; // optional, e.g. "jd.txt"
}