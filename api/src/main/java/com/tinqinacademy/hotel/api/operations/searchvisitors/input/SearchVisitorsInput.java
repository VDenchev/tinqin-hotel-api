package com.tinqinacademy.hotel.api.operations.searchvisitors.input;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
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
@ToString
@Builder
public class SearchVisitorsInput {

  @JsonUnwrapped
  private VisitorDetailsInput visitorDetailsInput;

  private String roomNo;
}
