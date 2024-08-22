package com.tinqinacademy.hotel.api.operations.removebooking.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
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

  @JsonIgnore
  @UUID(message = "Booking id has to be a valid UUID string")
  @NotBlank(message = "Booking id cannot be blank")
  private String bookingId;

  @UUID(message = "User id has to be a valid UUID string")
  @NotBlank(message = "User id cannot be blank")
  private String userId;
}
