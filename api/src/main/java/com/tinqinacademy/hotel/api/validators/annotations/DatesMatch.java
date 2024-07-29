package com.tinqinacademy.hotel.api.validators.annotations;

import com.tinqinacademy.hotel.api.validators.DatesMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DatesMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatesMatch {
  String message() default  "Dates must match";
  String startField();
  String endField();

  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    DatesMatch[] value();
  }
}
