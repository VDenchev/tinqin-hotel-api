package com.tinqinacademy.hotel.api.validators;

import com.tinqinacademy.hotel.api.validation.groups.NonMandatoryFieldsGroup;
import com.tinqinacademy.hotel.api.validators.annotations.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private static final Logger log = LoggerFactory.getLogger(EnumValidator.class);
  private Set<String> enumValues;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
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
