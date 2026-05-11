package com.coforge.hsbcdma.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RrNumberWhenCurrentValidator.class)
public @interface RrNumberWhenCurrent {
    String message() default "rrNumber is required and must be digits when demandTimeline is 'Current'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

