package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.core.converters.enums.PersBathroomTypeToApiBathroomType;
import com.tinqinacademy.hotel.core.converters.enums.BedToBedType;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoomToGetRoomOutput extends BaseConverter<Room, RoomDetailsOutput> {

  private final PersBathroomTypeToApiBathroomType persBathroomTypeToApiBathroomType;
  private final BedToBedType bedToBedType;

  @Override
  public RoomDetailsOutput doConvert(Room source) {
  RoomOutput output = RoomOutput.builder()
        .id(source.getId())
        .floor(source.getFloor())
        .price(source.getPrice())
        .bedCount(source.getBeds().size())
        .number(source.getNumber())
        .bathroomType(persBathroomTypeToApiBathroomType.convert(source.getBathroomType()))
        .bedSizes(source.getBeds().stream().map(bedToBedType::convert).toList())
        .build();
  return RoomDetailsOutput.builder()
      .roomOutput(output)
      .build();
  }
}
