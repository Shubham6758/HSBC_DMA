package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.*;
import com.coforge.hsbcdma.service.AddNewDemandService1;
import com.coforge.hsbcdma.service.DemandService.AddDemandDraftService;
import com.coforge.hsbcdma.service.DemandService.AddDemandDropdownOptionsService;
import com.coforge.hsbcdma.service.DemandService.AddNewDemandService;
import com.coforge.hsbcdma.service.DemandService.ViewDemandDraftService;
import com.coforge.hsbcdma.service.DropdownDataCacheService;
import com.coforge.hsbcdma.service.ExcelDataImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * This is a main controller which handles all requests for Add New Demand and has all end-points required for Add New Demand screen.
 * Created By : pratish.b
 */
@RestController
@RequestMapping("/addNewDemand")
public class AddNewDemandController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AddNewDemandController.class);

    @Autowired
    private  DropdownDataCacheService dropdownDataCacheService;

    @Autowired
    private AddNewDemandService1 addNewDemandService1;

    @Autowired
    private ExcelDataImportService excelDataImportService;

    @Autowired
    private ViewDemandDraftService viewDemandDraftService;

    @Autowired
    private AddDemandDraftService addDemandDraftService;


    @Autowired
    private AddNewDemandService addNewDemandService;

    @Autowired
    private AddDemandDropdownOptionsService optionsService;

//    @GetMapping("/home")
//    public ResponseEntity<?> home() throws Exception{
//        return success(dropdownDataCacheService.getDrodownDTOCache());
//    }

    // STEP 1: generate demand IDs using rr draft AUTO_INCREMENT, return step1 + assignments
    @PostMapping("/step1/next")
    public ResponseEntity<?> step1Next(@Valid @RequestBody AddNewDemandStep1DTO dto) {
        Step1NextResponseDTO resp = addNewDemandService.step1NextGenerateIds(dto);
        return success(resp);
    }

    // STEP 2: insert all data into add_demands, cleanup internal rr draft rows + internal draft header rows
    @PostMapping(value = "/step2/submit/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> step2Submit(
            @RequestPart("payload") String payload,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        Step2SubmitPayloadDTO dto =
                new ObjectMapper().readValue(payload, Step2SubmitPayloadDTO.class);

        PublishResponseDTO resp = addNewDemandService.step2SubmitFinal(dto, files);
        return success(resp);
    }
//    @PostMapping("/step1")
//    public ResponseEntity<?> step1Form(@RequestBody AddDemandRequestDTO addNewDemandDTO){
//        logger.info("****** Inside step1Form method of AddNewDemandController.");
//        addNewDemandService.buildDemandIdsToNumberOfPositions1(addNewDemandDTO); //To add number.For Ex: For LOG ET, demandId would be ET-502
//        return success(addNewDemandDTO);
//    }

//    @PostMapping("/step2")
//   //public ResponseEntity<?> step2Form(@RequestParam("file") MultipartFile file, @Validated(Stages.Stage2.class) @ModelAttribute AddNewDemandDTO addNewDemandDTO) throws IOException {
//    public ResponseEntity<?> step2Form(@RequestPart(value = "file") MultipartFile file , @Validated(Stages.Stage2.class) @RequestPart AddNewDemandDTO addNewDemandDTO) throws IOException{
//
//        logger.info("****** Inside step2Form method of AddNewDemandController.");
//        addNewDemandService.insertNewDemandsInDB(file, addNewDemandDTO);
//        return success(addNewDemandDTO);
//    }




    @PostMapping(
            value = "/step2",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> step2Form(
            @RequestPart("files") List<MultipartFile> files,
             @RequestPart("data") AddDemandRequestDTO addNewDemandDTO
    ) throws IOException {

        logger.info("****** Inside step2Form method of AddNewDemandController.");

        // Validate file count with noOfPositions (example field name)
        Integer noOfPositions = addNewDemandDTO.getNoOfPositions();
        if (noOfPositions != null && files != null && files.size() != noOfPositions) {
            return ResponseEntity.badRequest().body(
                    "Files count (" + files.size() + ") must match noOfPositions (" + noOfPositions + ")"
            );
        }

//        addNewDemandService.insertIntoDB1(files, addNewDemandDTO);
        return success(addNewDemandDTO);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importExcelSheet(@RequestParam("file") MultipartFile file) throws Exception {
        String result = excelDataImportService.importDemandsFromExcelIntoDB(file);
        return success(result);

    }

    //This is dummy method for testing purpose only. Not to be used in application.
    //Created by : pratish.b
//    @PostMapping("/dummy")
//    public ResponseEntity<?> dummyForm(@RequestParam("file") MultipartFile file, @Valid @ModelAttribute DummyDTO dummydDTO) throws IOException {
//        logger.info("****** Inside dummyForm method of AddNewDemandController.");
//        addNewDemandService.dummyInsertDB(file, dummydDTO);
//        return success(dummydDTO);
//    }
//
//    //This is dummy method for testing purpose only. Not to be used in application.
//    //Created by : pratish.b
//    @GetMapping("/dummy/getRecord")
//    public ResponseEntity<?> dummyData() throws IOException {
//        logger.info("****** Inside dummyData method of AddNewDemandController.");
//        DummyDTO dummyDTO = addNewDemandService.getDummyDataFromDB();
//        return success(dummyDTO);
//    }
/*    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFileAndSaveData(@Validated(Stages.Stage2.class) @ModelAttribute("newDemandAttr") AddNewDemandDTO addNewDemandDTO){
        addNewDemandService.insertNewDemandsInDB(addNewDemandDTO);
        return success(addNewDemandDTO);
    }*/
/*    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file,
                                         @RequestPart("data") AddNewDemandDTO addNewDemandDTO){
        return create(ResponseEntity.status(HttpStatus.CREATED).build());
    }*/



//    @GetMapping("/demands")
//    public ResponseEntity<?> getAllDemands(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        PageResponse getAllDemandsResponse = addNewDemandService1.getAllDemands1(page, size);
//        return success(getAllDemandsResponse);
//    }

    @GetMapping("/demands")
    public ResponseEntity<?> getAllDemands1(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long hbuId
    ) {
        PageResponse getAllDemandsResponse = addNewDemandService1.getAllDemands2(page, size,hbuId);
        return success(getAllDemandsResponse);
    }

    @PostMapping(
            value = "/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PageResponse search(
            @RequestBody DemandsFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return addNewDemandService1.searchDemands(filter, page, size);
    }


    @PutMapping(value = "/demands/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editDemand(
            @PathVariable("id") Long id,
            @RequestPart("payload") String payload,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {

        EditDemandPayloadDTO dto = new ObjectMapper().readValue(payload, EditDemandPayloadDTO.class);
        EditDemandResponseDTO resp = addNewDemandService.editDemandById(id, dto, files);
        return success(resp);
    }


    @PostMapping("/demands/{id}/copy")
    public ResponseEntity<?> copyDemand(@PathVariable Long id) {
        AddDemandCopyDTO copiedDemand = addNewDemandService.copyDemand(id);
        return ResponseEntity.ok(copiedDemand);
    }



    @GetMapping("/home")
    public ResponseEntity<AddDemandDropdownOptionsDTO> getDropdownOptions() {
        return ResponseEntity.ok(optionsService.getAllAsOptions());
    }
}
