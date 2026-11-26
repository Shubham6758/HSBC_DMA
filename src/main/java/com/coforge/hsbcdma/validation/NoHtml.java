package com.coforge.hsbcdma.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoHtmlValidator.class)
@Documented
public @interface NoHtml {

    String message() default
            "HTML or Script content is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
