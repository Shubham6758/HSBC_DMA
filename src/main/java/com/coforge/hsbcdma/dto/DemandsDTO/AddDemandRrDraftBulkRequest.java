package com.coforge.hsbcdma.dto.DemandsDTO;


import com.coforge.hsbcdma.validation.NoHtml;
import com.coforge.hsbcdma.validation.ValidFileName;
import lombok.Data;

import java.util.List;

@Data
public class AddDemandRrDraftBulkRequest {
    private List<RrDraftRequest> rrDrafts;


    // NEW (same pattern as final demand)
    private Boolean useSingleJdForAll;

    private String singleJdFileName;
    private String singleJdText;
    private String singleFilenameHint;


    @Data
    public static class RrDraftRequest {
        private String rrNumber;
        @ValidFileName
        private String fileName;     // JD file position in files[]
        private String jdText;// optional alternative
        @NoHtml
        private String filenameHint;   // optional


        // NEW
        private Boolean isSubconRR;

    }
}

