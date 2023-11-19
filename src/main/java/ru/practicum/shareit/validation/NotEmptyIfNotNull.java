package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyIfNotNullValidator.class)
public @interface NotEmptyIfNotNull {
    String message() default "{This parameter should not be empty}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
