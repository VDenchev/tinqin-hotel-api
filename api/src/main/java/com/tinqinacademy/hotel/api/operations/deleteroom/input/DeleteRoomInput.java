package com.tinqinacademy.hotel.api.operations.deleteroom.input;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tinqinacademy.hotel.api.base.OperationInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DeleteRoomInput implements OperationInput {

  @JsonValue
  @UUID(message = "Id has to be a valid UUID string")
  private String id;
}
