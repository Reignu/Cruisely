package com.cruisely.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static com.cruisely.common.I18n.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotEmpty(message = CONSTRAINT_NOT_EMPTY)
@Size(max = 64, message = NAME_SIZE)
@Pattern(regexp = ValidationRegex.NAME, message = REGEX_INVALID_NAME)
public @interface Name {
    String message() default REGEX_INVALID_NAME;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
