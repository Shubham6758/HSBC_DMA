package com.coforge.hsbcdma.controller;


import com.coforge.hsbcdma.dto.OnboardingOLDDTO;
import com.coforge.hsbcdma.dto.SelectionDTO;
import com.coforge.hsbcdma.serviceImpl.SelectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SelectionController exposes selection endpoint under /api/v1/demands/{id}/select.
 */
@RestController
@RequestMapping("/api/v1/demands")
public class SelectionController {
    private final SelectionService selectionService;

    public SelectionController(SelectionService selectionService) { this.selectionService = selectionService; }

    /**
     * PMO selection of candidate for demand.
     * Created by: Pooja.I
     */
    @PostMapping("/{id}/select")
    public ResponseEntity<OnboardingOLDDTO> select(@PathVariable("id") Long demandId, @Valid @RequestBody SelectionDTO selectionDTO) {
        return ResponseEntity.ok(selectionService.select(demandId, selectionDTO));
    }
}
