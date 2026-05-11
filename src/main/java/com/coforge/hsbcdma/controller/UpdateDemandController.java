package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.UpdateDemandDTO;
import com.coforge.hsbcdma.service.UpdateDemandService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * This is a main controller which handles all requests for Add New Demand and has all end-points required for Add New Demand screen.
 * Created By : pratish.b
 */
@RestController
@RequestMapping("/demandsheet")
public class UpdateDemandController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(UpdateDemandController.class);

    @Autowired
    private UpdateDemandService updateDemandService;

    @GetMapping("/demands")
    public ResponseEntity<?> getAllDemands(){

        return success(updateDemandService.fetchAllDemands());
    }

    @GetMapping("/getDemand/{id}")
    public ResponseEntity<?> fetchDemand(@PathVariable Long id){
        return success(updateDemandService.fetchDemandById(id));
    }

    @PutMapping("/saveDemand")
    public ResponseEntity<?> updateDemand(@RequestPart(value="file", required = false) MultipartFile file,@Valid @RequestPart UpdateDemandDTO updateDemandDTO) throws IOException {
        updateDemandService.updateDemand(file,updateDemandDTO);
        return success("Demand updated successfully");
    }
    /*@PutMapping("/saveDemand")
    public ResponseEntity<?> updateDemand(@RequestParam(value="file", required = false) MultipartFile file, @Valid @ModelAttribute UpdateDemandDTO updateDemandDTO) throws IOException {
        updateDemandService.updateDemand(file,updateDemandDTO);
        return success("Demand updated successfully");
    }*/
}
