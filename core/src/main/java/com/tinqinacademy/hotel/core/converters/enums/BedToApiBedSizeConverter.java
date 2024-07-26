package com.tinqinacademy.hotel.core.converters.enums;

import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import org.springframework.stereotype.Component;

@Component
public class BedToApiBedSizeConverter extends BaseConverter<Bed, BedType> {
  @Override
  protected BedType doConvert(Bed source) {
    return BedType.getByCode(source.getBedSize().getCode());
  }
}
