package com.coforge.hsbcdma.resource.Controller;


import com.coforge.hsbcdma.resource.Entity_Resource_Sheet.ResourceData;
import com.coforge.hsbcdma.resource.Service_Resource_Sheet.ResourceDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource-data")
public class ResourceDataController {

    private final ResourceDataService service;

    public ResourceDataController(ResourceDataService service) {
        this.service = service;
    }

    // ✅ CREATE (Frontend will hit this)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ResourceData data) {

        ResourceData saved = service.save(data);

        return ResponseEntity.ok(Map.of(
                "message", "Resource data saved successfully",
                "id", saved.getId()
        ));
    }

    // ✅ GET ALL
    @GetMapping
    public List<ResourceData> getAll() {
        return service.getAll();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResourceData getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ResourceData data
    ) {

        ResourceData updated = service.update(id, data);

        return ResponseEntity.ok(Map.of(
                "message", "Resource data updated successfully",
                "id", updated.getId()
        ));
    }


    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}