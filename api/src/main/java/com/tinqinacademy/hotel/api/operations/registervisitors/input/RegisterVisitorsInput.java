package com.tinqinacademy.hotel.api.operations.registervisitors.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegisterVisitorsInput implements OperationInput {

  @NotNull(message = "Visitors cannot be empty")
  private List<@Valid VisitorDetailsInput> visitors;

  @JsonIgnore
  @UUID(message = "BookingId has to be a valid UUID string")
  private String bookingId;
}
