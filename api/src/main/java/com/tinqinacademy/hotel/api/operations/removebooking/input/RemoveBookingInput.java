package com.tinqinacademy.hotel.api.operations.removebooking.input;

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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RemoveBookingInput implements OperationInput {

  @JsonValue
  @UUID(message = "BookingId has to be a valid UUID string")
  private String bookingId;
}
