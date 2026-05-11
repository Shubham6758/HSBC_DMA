package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandDTO;
import com.coforge.hsbcdma.serviceImpl.DemandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DemandController exposes Demand endpoints under /api/v1/demands.
 */
@RestController
@RequestMapping("/api/v1/demands")
public class DemandController {
    private final DemandService demandService;

    public DemandController(DemandService demandService) { this.demandService = demandService; }

    /**
     * Create demand endpoint.
     * Created by: Pooja.I
     */
    @PostMapping
    public ResponseEntity<DemandDTO> create(@Valid @RequestBody DemandDTO dto) {
        return ResponseEntity.ok(demandService.create(dto));
    }

    /**
     * Get demand by id endpoint.
     * Created by: Pooja.I
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandDTO> get(@PathVariable("id") Long id) {

        return ResponseEntity.ok(demandService.get(id));
    }

    /**
     * List all demands endpoint.
     * Created by: Pooja.I
     */
    @GetMapping
    public ResponseEntity<List<DemandDTO>> list() {
        return ResponseEntity.ok(demandService.list());
    }

    /**
     * Update demand endpoint.
     * Created by: Pooja.I
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemandDTO> update(@PathVariable("id") Long id, @Valid @RequestBody DemandDTO dto) {
        return ResponseEntity.ok(demandService.update(id, dto));
    }

    /**
     * Delete demand endpoint.
     * Created by: Pooja.I
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        demandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
