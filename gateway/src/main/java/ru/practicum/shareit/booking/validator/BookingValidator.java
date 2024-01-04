package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingValidator implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {

    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookItemRequestDto bookItemRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookItemRequestDto.getStart();
        LocalDateTime end = bookItemRequestDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
