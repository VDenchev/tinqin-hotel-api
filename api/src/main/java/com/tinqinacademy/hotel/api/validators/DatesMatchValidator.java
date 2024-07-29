package com.tinqinacademy.hotel.api.validators;

import com.tinqinacademy.hotel.api.validators.annotations.DatesMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DatesMatchValidator implements ConstraintValidator<DatesMatch, Object> {
  private String startField;
  private String endField;

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    LocalDate startFieldValue = (LocalDate) new BeanWrapperImpl(o)
        .getPropertyValue(startField);
    LocalDate endFieldValue = (LocalDate) new BeanWrapperImpl(o)
        .getPropertyValue(endField);

    if (startFieldValue == null || endFieldValue == null) {
      return true;
    }

    return endFieldValue.isAfter(startFieldValue);
  }

  @Override
  public void initialize(DatesMatch constraintAnnotation) {
    this.startField = constraintAnnotation.startField();
    this.endField = constraintAnnotation.endField();
  }
}
