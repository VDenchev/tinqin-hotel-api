package com.tinqinacademy.hotel.core.converters.enums;

import com.tinqinacademy.hotel.api.enums.BathroomType;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class PersBathroomTypeToApiBathroomType extends BaseConverter<com.tinqinacademy.hotel.persistence.enums.BathroomType,BathroomType> {
  @Override
  protected BathroomType doConvert(com.tinqinacademy.hotel.persistence.enums.BathroomType source) {
    return BathroomType.getByCode(source.getCode());
  }
}
