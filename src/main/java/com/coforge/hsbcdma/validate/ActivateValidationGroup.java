package com.coforge.hsbcdma.validate;

import com.coforge.hsbcdma.dto.AddNewDemandDTO;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivateValidationGroup
        implements DefaultGroupSequenceProvider<AddNewDemandDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ActivateValidationGroup.class);


    @Override
    public List<Class<?>> getValidationGroups(Class<?> klass, AddNewDemandDTO dto) {
        List<Class<?>> groups = new ArrayList<>();
        // The DTO class MUST be first (represents the Default group)
        groups.add(AddNewDemandDTO.class);
        logger.info("********* In ActivateValidationGroup - getValidationGroups *********  1");
        // Decide your order (Stage1 first, then Stage2 if applicable)
        groups.add(Stages.Stage1.class);

        boolean timelineIsCurrent = dto != null
                && "Current".equalsIgnoreCase(dto.getDemandTimeline());
        boolean hasRRItems = dto != null
                && dto.getDemandRRDTOList() != null
                && !dto.getDemandRRDTOList().isEmpty();

        if (timelineIsCurrent && hasRRItems) {
            groups.add(Stages.Stage2.class);
            logger.info("********* In ActivateValidationGroup - getValidationGroups *********  2");
        }

        return groups;
    }
}

