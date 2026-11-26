package com.coforge.hsbcdma.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileNameValidator
        implements ConstraintValidator<ValidFileName, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() > 255) {
            return false;
        }

        return !(value.contains("..")
                || value.contains("/")
                || value.contains("\\"));
    }
}
