package com.coforge.hsbcdma.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RrNumberValidator
        implements ConstraintValidator<ValidRrNumber, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        value = value.trim();

        if (value.length() > 50) {
            return false;
        }

        return value.matches("^[a-zA-Z0-9/_-]+$");
    }
}
