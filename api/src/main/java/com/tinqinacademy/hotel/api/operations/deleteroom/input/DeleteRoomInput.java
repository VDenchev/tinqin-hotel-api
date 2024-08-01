package com.tinqinacademy.hotel.api.operations.deleteroom.input;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tinqinacademy.hotel.api.base.OperationInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DeleteRoomInput implements OperationInput {

  @JsonValue
  private String id;
}
