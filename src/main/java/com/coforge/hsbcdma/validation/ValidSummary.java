package com.coforge.hsbcdma.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SummaryValidator.class)
@Documented
public @interface ValidSummary {

    String message() default "Invalid Summary";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
