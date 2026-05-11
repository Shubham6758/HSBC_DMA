
package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.RdgManagementRequestDto;
import com.coforge.hsbcdma.dto.RdgManagementResponseDto;
import com.coforge.hsbcdma.service.RdgManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
/**
 * This is a main controller which handles all requests for Rdg Team Management and has all end-points required for Rdg Management screen.
 * Created By : bhushan.k
 */


@RestController
@RequestMapping("/api/rdgManagement")
public class RdgManagementController extends BaseController {

    private final RdgManagementService rdgManagementService;
    private static final Logger logger = LoggerFactory.getLogger(RdgManagementController.class);


    public RdgManagementController(RdgManagementService rdgManagementService) {
        this.rdgManagementService = rdgManagementService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        logger.info("****** Inside getAll method of RdgManagementController.");
        return success(rdgManagementService.getAll());
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestPart("file") MultipartFile[] files,
            @RequestPart("dtos") String dtosJson) throws Exception{

        logger.info("****** Inside save method of RdgManagementController.");
        List<RdgManagementRequestDto> dtos =
                new ObjectMapper().readValue(dtosJson, new TypeReference<List<RdgManagementRequestDto>>() {}
                );

        rdgManagementService.create(files, dtos);
        return create(dtos);
    }


    @GetMapping("/view/by-demand/{demandId}")
    public ResponseEntity<?> viewByDemandId(@PathVariable String demandId) {
        logger.info("****** Inside view/by-demand method of RdgManagementController.");
        List<RdgManagementResponseDto> response = rdgManagementService.getDataByDemandId(demandId);
        return success(response);
    }

    @DeleteMapping("/delete/{demandId}")
    public ResponseEntity<?> deleteByDemandId(@PathVariable String demandId){
        logger.info("****** Inside delete method of RdgManagementController.");
        rdgManagementService.deleteByDemandId(demandId);
        return ResponseEntity.noContent().build();
    }
}