package com.tinqinacademy.hotel.api.operations.addroom.input;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import jakarta.validation.Valid;
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
public class AddRoomInput implements OperationInput {

  @JsonUnwrapped
  @Valid
  private RoomInput roomInput;
}
