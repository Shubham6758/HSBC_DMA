package com.coforge.hsbcdma.audit.History;


import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryController {

    private final HistoryService demandHistoryService;

    /**
     * BASIC Demand History:
     * GET /api/demands/{demandId}/history?page=0&size=20
     * GET /api/demands/{demandId}/history?page=0&size=20&includeDiff=true
     */

    @GetMapping("/demands/{demandId}/history")
    public PageResponse getDemandHistory(
            @PathVariable Long demandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeDiff
    ) {
        return demandHistoryService.getDemandHistory(demandId, page, size, includeDiff);
    }


    @GetMapping("/profile/{profileId}/history")
    public PageResponse getProfileHistory(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeDiff
    ) {
        return demandHistoryService.getProfileHistory(profileId, page, size, includeDiff);
    }

}