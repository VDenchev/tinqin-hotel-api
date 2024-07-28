package com.tinqinacademy.hotel.core.converters.room;


import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.core.converters.enums.ApiBathroomTypeToPersistenceBathroomType;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomInputToRoom implements Converter<RoomInput, Room> {

  private final ApiBathroomTypeToPersistenceBathroomType apiBathroomTypeToPersistenceBathroomType;

  @Override
  public Room convert(RoomInput source) {
    return Room.builder()
        .number(source.getRoomNo())
        .floor(source.getFloor())
        .price(source.getPrice())
        .bathroomType(
            apiBathroomTypeToPersistenceBathroomType.convert(source.getBathroomType())
        ).build();
  }
}
