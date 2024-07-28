package com.tinqinacademy.hotel.core.converters.user;

import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import org.springframework.stereotype.Component;

@Component
public class SignUpInputToUser extends BaseConverter<SignUpInput, User> {

  @Override
  protected User doConvert(SignUpInput source) {
    return User.builder()
        .firstName(source.getFirstName())
        .lastName(source.getLastName())
        .phoneNumber(source.getPhoneNo())
        .email(source.getEmail())
        .password(source.getPassword())
        .build();
  }
}
