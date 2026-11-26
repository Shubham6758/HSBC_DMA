package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.OtherDataRecord;
import com.coforge.hsbcdma.revenuemargin.entity.OtherDataEntity;
import com.coforge.hsbcdma.revenuemargin.repository.OtherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtherDataService {

    @Autowired
    private OtherDataRepository otherDataRepository;
    public void deleteAllRecords() {

        otherDataRepository.deleteAll();

    }
    public long getRecordCount() {

        return otherDataRepository.count();

    }
    public void saveRecords(List<OtherDataRecord> records) {

        for (OtherDataRecord record : records) {

            OtherDataEntity entity =
                    new OtherDataEntity();

            entity.setEmpId(record.getEmpId());
            entity.setPsid(record.getPsid());
            entity.setName(record.getName());
            entity.setCoforgeBand(record.getCoforgeBand());
            entity.setLob(record.getLob());
            entity.setGbGi(record.getGbGi());
            entity.setBfLevel3(record.getBfLevel3());
            entity.setBfLevel4(record.getBfLevel4());
            entity.setBfLevel5(record.getBfLevel5());
            entity.setPricingModel(record.getPricingModel());
            entity.setContractStartDate(record.getContractStartDate());
            entity.setContractEndDate(record.getContractEndDate());

            otherDataRepository.save(entity);
        }

        System.out.println(
                "Other Data Records Saved = "
                        + records.size()
        );
    }
}