package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.core.converters.enums.ApiBathroomTypeConverter;
import com.tinqinacademy.hotel.core.converters.enums.BedToApiBedSizeConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoomToGetRoomOutputConverter extends BaseConverter<Room, RoomDetailsOutput> {

  private final ApiBathroomTypeConverter apiBathroomTypeConverter;
  private final BedToApiBedSizeConverter bedToApiBedSizeConverter;

  @Override
  public RoomDetailsOutput doConvert(Room source) {
  RoomOutput output = RoomOutput.builder()
        .id(source.getId())
        .floor(source.getFloor())
        .price(source.getPrice())
        .bedCount(source.getBeds().size())
        .number(source.getNumber())
        .bathroomType(apiBathroomTypeConverter.convert(source.getBathroomType()))
        .bedSizes(source.getBeds().stream().map(bedToApiBedSizeConverter::convert).toList())
        .build();
  return RoomDetailsOutput.builder()
      .roomOutput(output)
      .build();
  }
}
