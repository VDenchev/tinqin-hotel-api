package com.tinqinacademy.hotel.core.mappers;

import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true))
public interface UserMapper {

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "password", source = "hashedPassword")
  User fromSignUpInputToUser(SignUpInput signUpInput, String hashedPassword);

  SignUpOutput fromUserToSignUpOutput(User user);
}
