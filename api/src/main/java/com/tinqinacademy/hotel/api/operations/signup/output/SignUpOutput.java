package com.tinqinacademy.hotel.api.operations.signup.output;

import com.tinqinacademy.hotel.api.base.OperationOutput;
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
public class SignUpOutput implements OperationOutput {

  private String id;
}
