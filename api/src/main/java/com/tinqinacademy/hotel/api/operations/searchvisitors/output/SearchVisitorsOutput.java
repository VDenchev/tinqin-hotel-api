package com.tinqinacademy.hotel.api.operations.searchvisitors.output;

import com.tinqinacademy.hotel.api.models.output.VisitorDetailsOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SearchVisitorsOutput {

  private List<VisitorDetailsOutput> visitors;
}
