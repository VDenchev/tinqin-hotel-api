package com.tinqinacademy.hotel.api.operations.checkavailablerooms.output;

import com.tinqinacademy.hotel.api.base.OperationOutput;
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
public class AvailableRoomsOutput implements OperationOutput {

  private List<String> roomIds;
}
