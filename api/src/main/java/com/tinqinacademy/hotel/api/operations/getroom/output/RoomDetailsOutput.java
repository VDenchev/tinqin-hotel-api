package com.tinqinacademy.hotel.api.operations.getroom.output;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoomDetailsOutput implements OperationOutput {

  @JsonUnwrapped
  private RoomOutput roomOutput;
}
