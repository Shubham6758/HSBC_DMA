package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.M4RateRecord;
import com.coforge.hsbcdma.revenuemargin.entity.M4RateEntity;
import com.coforge.hsbcdma.revenuemargin.repository.M4RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class M4RateService {

    @Autowired
    private M4RateRepository m4RateRepository;
    public void deleteAllRecords() {

        m4RateRepository.deleteAll();

    }
    public long getRecordCount() {

        return m4RateRepository.count();

    }
    public void saveRecords(
            List<M4RateRecord> records) {

        for (M4RateRecord record : records) {

            M4RateEntity entity =
                    new M4RateEntity();

            entity.setRef(
                    record.getRef()
            );

            entity.setRoleRef(
                    record.getRoleRef()
            );

            entity.setRoleTitle(
                    record.getRoleTitle()
            );
            entity.setMaxDayRateUsd(
                    record.getMaxDayRateUsd()
            );

            m4RateRepository.save(
                    entity
            );

        }

        System.out.println(
                "M4 Records Saved = "
                        + records.size()
        );

    }
}