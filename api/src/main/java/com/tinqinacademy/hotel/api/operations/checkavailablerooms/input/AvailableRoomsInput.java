package com.tinqinacademy.hotel.api.operations.checkavailablerooms.input;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.enums.BathroomType;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.validators.annotations.DatesMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@DatesMatch(
    startField = "startDate",
    endField = "endDate",
    message = "Start date must be before end date"
)
public class AvailableRoomsInput implements OperationInput {

  private Integer bedCount;
  private BathroomType bathroomType;
  private List<BedType> bedSizes;
  private LocalDate startDate;
  private LocalDate endDate;
}
