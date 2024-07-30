package com.tinqinacademy.hotel.api.operations.removebooking.input;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tinqinacademy.hotel.api.base.OperationInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RemoveBookingInput implements OperationInput {

  @JsonValue
  private UUID bookingId;
}
