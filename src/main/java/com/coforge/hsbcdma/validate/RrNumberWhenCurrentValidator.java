package com.coforge.hsbcdma.validate;

import com.coforge.hsbcdma.dto.AddNewDemandDTO;
import com.coforge.hsbcdma.dto.DemandRRDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class RrNumberWhenCurrentValidator
        implements ConstraintValidator<RrNumberWhenCurrent, AddNewDemandDTO> {

    @Override
    public boolean isValid(AddNewDemandDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true; // bean-level null handled elsewhere

        // Only enforce when demandTimeline == "Current"
        if (!"Current".equalsIgnoreCase(dto.getDemandTimeline())) {
            return true;
        }

        List<DemandRRDTO> items = dto.getDemandRRDTOList();
        if (items == null || items.isEmpty()) {
            // If you also require the list itself in 'Current', keep your @NotNull(groups=Stage2)
            // Here, treat null/empty as invalid if that's your business rule:
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("demandRRDTOList must not be empty when demandTimeline is 'Current'")
                    .addPropertyNode("demandRRDTOList")
                    .addConstraintViolation();
            return false;
        }

        boolean valid = true;
        ctx.disableDefaultConstraintViolation();

        for (int i = 0; i < items.size(); i++) {
            DemandRRDTO rr = items.get(i);
            // Validate rrNumber presence
            if (rr == null || rr.getRrNumber() == null) {
                ctx.buildConstraintViolationWithTemplate("RRNumber is required when DemandTimeLine is 'Current'")
                        .addPropertyNode("demandRRDTOList")
                        .inIterable().atIndex(i)
                        .addPropertyNode("rrNumber")
                        .addConstraintViolation();
                valid = false;
                continue;
            }
            // Digits check: 0..10 integer digits, no fraction
            String s = String.valueOf(rr.getRrNumber());
            if (!s.matches("^\\d{1,10}$")) {
                ctx.buildConstraintViolationWithTemplate("RRNumber must be numeric with up to 10 digits")
                        .addPropertyNode("demandRRDTOList")
                        .inIterable().atIndex(i)
                        .addPropertyNode("rrNumber")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}

