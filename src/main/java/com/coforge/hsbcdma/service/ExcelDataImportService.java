package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.entity.AddNewDemand;
import com.coforge.hsbcdma.repository.AddNewDemandRepository;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a service class to upload demands from excel sheet into database.
 * Created By : pratish.b
 */
@Service
public class ExcelDataImportService {

    @Autowired
    private AddNewDemandRepository addNewDemandRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExcelDataImportService.class);

    //@Transactional
    public String importDemandsFromExcelIntoDB(MultipartFile file) throws IOException {

        Sheet sheet = null;
        List<AddNewDemand> demandList = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx files are supported");
        }
            int limitInsert = 0;
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row == null) continue;

                if (row.getRowNum() == 0) continue;

                if(addNewDemandRepository.findByDemandId(getCellData(workbook,row, 0)).isPresent()) {
                    continue;
                }

                AddNewDemand demand = new AddNewDemand();

                demand.setDemandId(getCellData(workbook,row, 0));
                demand.setLob(getCellData(workbook,row, 2));
                demand.setPrimarySkills(getCellData(workbook,row, 8));
                demand.setSecondarySkills(getCellData(workbook,row, 9));
                String str1 = getCellData(workbook,row, 1);

                if (DateUtil.isCellDateFormatted(getCell(workbook,row, 1))) {
                    LocalDate date = getCell(workbook,row, 1).getLocalDateTimeCellValue().toLocalDate();
                    //logger.info("****** cell data string2 is "+date);
                    demand.setDemandReceivedDate(date);
                }

                demand.setHiringManager(getCellData(workbook,row, 13));
                demand.setSalesSpoc(getCellData(workbook,row, 14));
                //demand.setDeliveryManager();
                demand.setPmo(getCellData(workbook,row, 17));
                demand.setHbu(getCellData(workbook,row, 19));
                demand.setDemandType(getCellData(workbook,row, 5));
                //demand.setDemandTimeline();
                demand.setProdProgramName(getCellData(workbook,row, 4));
                demand.setExperience(getCellData(workbook,row, 10));
                demand.setPriority(getCellData(workbook,row, 11));
                demand.setDemandLocation(getCellData(workbook,row, 12));
                //demand.setPriorityComment();
                demand.setPm(getCellData(workbook,row, 18));
                demand.setBand(getCellData(workbook,row, 21));
                demand.setP1Age(getCellData(workbook,row, 22));
                //demand.setCurrentProfileShared();
               // demand.setExternalInternal();
                demand.setStatus(getCellData(workbook,row, 23));
                demand.setPmoSpoc(getCellData(workbook,row, 17));

                if(!getCellData(workbook,row, 20).isBlank()){
                    String str = digitsOnly(getCellData(workbook,row, 20));
                    if(!str.isBlank()){
                        demand.setRrNumber(Long.parseLong(str));
                    }
                }

                demandList.add(demand);

                //try {
                    addNewDemandRepository.save(demand);    // or saveAll(entities)
                    addNewDemandRepository.flush(); // force SQL now
                //} catch (org.springframework.dao.DataIntegrityViolationException ex) {
                   /* Throwable root = ex.getRootCause();
                    if (root instanceof org.hibernate.exception.ConstraintViolationException hce) {
                        String msg = hce.getSQLException().getMessage();
                        logger.error("SQL message:{} . Entity data {}. Row number {}",msg,demand.toString(),limitInsert);
                        // Optional: parse number from message (DB dependent)
                    }*/
                   // throw new RuntimeException(ex);
                //}
                limitInsert++;

                //****************
               /* String demandId = getCellData(workbook,row, 0); //row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue();
                String demandReceivedDate = getCellData(workbook,row, 1);
                String lob = getCellData(workbook,row, 2);
                String businessFunction = getCellData(workbook,row, 3);
                String programName = getCellData(workbook,row, 4);
                String demandType = getCellData(workbook,row, 5);
                String cluster = getCellData(workbook,row, 6);
                String skillCluster = getCellData(workbook,row, 7);
                String primarySkill = getCellData(workbook,row, 8);
                String secondarySkill = getCellData(workbook,row, 9);
                String experience = getCellData(workbook,row, 10);
                String priority = getCellData(workbook,row, 11);
                String demandLocation = getCellData(workbook,row, 12);
                String hiringManager = getCellData(workbook,row, 13);
                String salesSpoc = getCellData(workbook,row, 14);
                String p1FlagDate = getCellData(workbook, row, 15);
                String priorityComment = getCellData(workbook,row, 16);
                String pmoSpoc = getCellData(workbook,row, 17);
                String pm = getCellData(workbook,row, 18);
                String hbu = getCellData(workbook,row, 19);
                String rr_number = getCellData(workbook,row, 20);
                String band = getCellData(workbook,row, 21);
                String p1Age = getCellData(workbook,row, 22);
                String status = getCellData(workbook,row, 23);*/

                //logger.info("Demand Id is "+demandId+"#############@#@#@#" + demandReceivedDate +"********** LOB is "+ lob+"***** Business Function "+businessFunction);
                //break;


                //if(limitInsert == 15) break;
            }
            /*if(!demandList.isEmpty()){
                addNewDemandRepository.saveAll(demandList);//Insert all demands into DB
            }*/

            //logger.info("Total number of rows "+sheet.getPhysicalNumberOfRows());
            logger.info("Total number of rows "+limitInsert);
        }

                //return "Total rows inserted : "+sheet.getPhysicalNumberOfRows();
        return "Total rows inserted : "+limitInsert;
    }

    private String getCellData(Workbook workbook,Row row, int columnIndex) {
        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        String columnData = "";

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell != null) {
            columnData = formatter.formatCellValue(row.getCell(columnIndex),evaluator).trim();
        }

        //logger.info("###### :"+columnData);
        return columnData;
    }

    private Cell getCell(Workbook workbook,Row row, int columnIndex){
        return row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    }

    private String digitsOnly(String s) {
        if (s == null) return "";
        return s.replaceAll("\\D+", ""); // removes all non-digits
    }


    /*private String getCellData(Row row, int column) {


        String columnData = "";
        Cell cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            // handle blank/missing cell
            switch (cell.getCellType()) {
                case STRING:
                    columnData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    //System.out.println(cell.getNumericCellValue());
                    columnData = String.valueOf(Double.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    //System.out.println(cell.getBooleanCellValue());
                    columnData = String.valueOf(Boolean.valueOf(cell.getBooleanCellValue()));
                    break;
                case FORMULA:
                    System.out.println(cell.getCellFormula());
                    columnData = String.valueOf(cell.getCellFormula());
                    // or evaluate the formula using FormulaEvaluator
                    break;
                default:
                    // BLANK / _NONE / ERROR
                    columnData ="";
                    //System.out.println("");
            }

        }
        logger.info("******$$$$$ :"+columnData);
        return columnData;
    }*/
}