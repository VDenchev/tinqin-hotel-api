package com.tinqinacademy.hotel.api.operations.signup.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class SignUpOutput {

  private UUID id;
}
