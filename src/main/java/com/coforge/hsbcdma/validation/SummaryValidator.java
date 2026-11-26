package com.coforge.hsbcdma.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SummaryValidator
        implements ConstraintValidator<ValidSummary, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() > 5000) {
            return false;
        }

        String lower = value.toLowerCase();

        return !(lower.contains("<")
                || lower.contains(">")
                || lower.contains("script")
                || lower.contains("javascript:"));
    }
}
