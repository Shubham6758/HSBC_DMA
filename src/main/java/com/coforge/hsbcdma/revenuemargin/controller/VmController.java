package com.coforge.hsbcdma.revenuemargin.controller;

import com.coforge.hsbcdma.revenuemargin.service.VmDumpService;
import com.coforge.hsbcdma.revenuemargin.service.importer.VmDumpImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class VmController {
    @Autowired
    private VmDumpImporter vmDumpImporter;
    @Autowired
    private VmDumpService vmDumpService;
    //@GetMapping("/vm/refresh")
    //public String refreshVm(
    //        Model model) throws Exception {

    //    model.addAttribute(
    //            "status",
    //            "Processing..."
    //    );

    //    vmDumpImporter.importData(
    //            "src/main/resources/sample-data/HSBCVendorManagementData.xlsx",
    //            "VM Dump"
    //    );
    //    model.addAttribute(
    //            "rowsLoaded",
    //            vmDumpService.getRecordCount()
    //    );
    //    model.addAttribute(
    //            "status",
    //            "Success"
    //    );

    //    return "vm-refresh";
    //}
}