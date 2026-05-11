package com.coforge.hsbcdma.dto.DemandsDTO;


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
        private Long rrNumber;
        private String fileName;     // JD file position in files[]
        private String jdText;         // optional alternative
        private String filenameHint;   // optional


        // NEW
        private Boolean isSubconRR;

    }
}

