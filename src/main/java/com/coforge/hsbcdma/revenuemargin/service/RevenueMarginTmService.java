package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.RevenueMarginTmRecord;
import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevenueMarginTmService {

    @Autowired
    private RevenueMarginTmRepository revenueMarginTmRepository;

    public long getRecordCount() {
        return revenueMarginTmRepository.count();
    }
    public void addNewResourcesFromVmDump() {revenueMarginTmRepository.addNewResources();
    }
    public void saveRecords(
            List<RevenueMarginTmRecord> records) {

        revenueMarginTmRepository.deleteAll();

        for (RevenueMarginTmRecord record : records) {

            RevenueMarginTmEntity entity =
                    new RevenueMarginTmEntity();

            entity.setEmpId(record.getEmpId());
            entity.setPsid(record.getPsid());
            entity.setName(record.getName());
            entity.setCtool(record.getCtool());

            revenueMarginTmRepository.save(entity);
        }

        System.out.println(
                "Revenue Margin TM Records Saved = "
                        + records.size()
        );
    }
}