package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = BookingValidator.class)
public @interface StartBeforeEnd {
    String message = "Start should be before end or not null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
