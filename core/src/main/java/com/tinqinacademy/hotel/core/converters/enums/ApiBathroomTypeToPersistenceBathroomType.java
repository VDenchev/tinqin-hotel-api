package com.tinqinacademy.hotel.core.converters.enums;

import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import org.springframework.stereotype.Component;

@Component
public class ApiBathroomTypeToPersistenceBathroomType extends BaseConverter<String, BathroomType> {

  @Override
  public BathroomType doConvert(String source) {
    return BathroomType.getByCode(source);
  }
}
