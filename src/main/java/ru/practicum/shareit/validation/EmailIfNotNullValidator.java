package ru.practicum.shareit.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailIfNotNullValidator implements ConstraintValidator<EmailIfNotNull, String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        String emailRegex = "^(.+)@(\\S+)$";
        Pattern pattern = Pattern.compile(emailRegex);

        return email == null || (!email.isEmpty() && pattern.matcher(email).matches());
    }
}