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

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegisterVisitorsInput implements OperationInput {

  @NotNull(message = "Visitors cannot be empty")
  @Valid
  private List<VisitorDetailsInput> visitors;

  @JsonIgnore
  private UUID bookingId;
}
