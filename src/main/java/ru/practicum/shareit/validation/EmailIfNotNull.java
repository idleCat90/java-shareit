package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailIfNotNullValidator.class)
public @interface EmailIfNotNull {
    String message() default "{Email is invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
