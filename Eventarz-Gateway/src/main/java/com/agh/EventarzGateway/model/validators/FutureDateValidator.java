package com.agh.EventarzGateway.model.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FutureDateValidator implements ConstraintValidator<FutureDate, String> {

    @Override
    public void initialize(FutureDate contactNumber) {
    }

    @Override
    public boolean isValid(String dateString, ConstraintValidatorContext cxt) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime eventDateObject = LocalDateTime.parse(dateString, dtf);
            return !eventDateObject.isBefore(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
