package com.coforge.hsbcdma.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RrNumberValidator.class)
@Documented
public @interface ValidRrNumber {

    String message() default
            "Invalid RR Number. Only letters, numbers, /, _ and - are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}