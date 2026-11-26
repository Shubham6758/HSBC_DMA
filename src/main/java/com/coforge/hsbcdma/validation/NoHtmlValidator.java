package com.coforge.hsbcdma.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoHtmlValidator
        implements ConstraintValidator<NoHtml, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        String lower = value.toLowerCase();

        return !(lower.contains("<")
                || lower.contains(">")
                || lower.contains("script")
                || lower.contains("javascript:")
                || lower.contains("onerror")
                || lower.contains("onload")
                || lower.contains("onclick")
                || lower.contains("onmouseover")
                || lower.contains("alert("));
    }
}
