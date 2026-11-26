package com.coforge.hsbcdma.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileNameValidator.class)
@Documented
public @interface ValidFileName {

    String message() default "Invalid file name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
