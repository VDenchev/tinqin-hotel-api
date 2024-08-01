package com.tinqinacademy.hotel.api.models.output;

import com.tinqinacademy.hotel.api.enums.BathroomType;
import com.tinqinacademy.hotel.api.enums.BedType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoomOutput {

  private String id;
  private String number;
  private BigDecimal price;
  private Integer floor;
  private List<BedType> bedSizes;
  private BathroomType bathroomType;
  private Integer bedCount;
  private DatesOccupied datesOccupied;
}
