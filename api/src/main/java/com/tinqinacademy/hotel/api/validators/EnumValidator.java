package com.tinqinacademy.hotel.api.validators;

import com.tinqinacademy.hotel.api.validators.annotations.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private Set<String> enumValues;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    return enumValues.contains(value);
  }

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
    this.enumValues = Stream.of(enumClass.getEnumConstants())
        .map(Enum::toString)
        .collect(Collectors.toSet());
  }
}
