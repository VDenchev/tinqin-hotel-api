package com.tinqinacademy.hotel.core.converters.enums;

import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import org.springframework.stereotype.Component;

@Component
public class PersistenceBathroomTypeConverter extends BaseConverter<com.tinqinacademy.hotel.api.enums.BathroomType, BathroomType> {

  @Override
  public BathroomType doConvert(com.tinqinacademy.hotel.api.enums.BathroomType source) {
    return BathroomType.getByCode(source.getCode());
  }
}
