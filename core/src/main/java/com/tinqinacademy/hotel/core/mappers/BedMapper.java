package com.tinqinacademy.hotel.core.mappers;

import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class BedMapper {

  public BedType fromBedToBedType(Bed bed) {
    return BedType.getByCode(bed.getBedSize().getCode());
  }

}
