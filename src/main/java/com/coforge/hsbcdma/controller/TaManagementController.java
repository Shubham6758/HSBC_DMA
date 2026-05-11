
package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.domain.model.ApiResponse;
import com.coforge.hsbcdma.dto.TaManagementRequestDto;
import com.coforge.hsbcdma.dto.TaManagementResponseDto;
import com.coforge.hsbcdma.service.TaManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This is a main controller which handles all requests for Ta Team Management and has all end-points required for Ta Management screen.
 * Created By : bhushan.k
 */

@RestController
@RequestMapping("/api/taManagement")
public class TaManagementController extends BaseController {

    private final TaManagementService taManagementService;
    private static final Logger logger = LoggerFactory.getLogger(TaManagementController.class);


    public TaManagementController(TaManagementService taManagementService) {
        this.taManagementService = taManagementService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        logger.info("****** Inside getAll method of TaManagementController.");
        return success(taManagementService.getAll());
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestPart("cvFile") MultipartFile[] cvFiles,
            @RequestPart("irsFile") MultipartFile[] irsFiles,
            @RequestPart("dtos") String dtosJson) throws Exception{
        logger.info("****** Inside save method of TaManagementController.");
        List<TaManagementRequestDto> dtos =
                new ObjectMapper().readValue(dtosJson, new TypeReference<List<TaManagementRequestDto>>() {}
                );
        taManagementService.create(cvFiles,irsFiles, dtos);
        return create(dtos);
    }

    @GetMapping("/view/by-demand/{demandId}")
    public ResponseEntity<?> viewByDemandId(@PathVariable String demandId) {
        logger.info("****** Inside view method of TaManagementController.");
        List<TaManagementResponseDto> response = taManagementService.getDataByDemandId(demandId);
        return  success(response);
    }

    @DeleteMapping("/delete/{demandId}")
    public ResponseEntity<?> deleteByDemandId(@PathVariable String demandId){
        logger.info("****** Inside delete method of TaManagementController.");
        taManagementService.deleteByDemandId(demandId);
        return ResponseEntity.noContent().build();
    }
}