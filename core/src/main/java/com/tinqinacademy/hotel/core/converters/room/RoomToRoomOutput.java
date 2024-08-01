package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomToRoomOutput extends BaseConverter<Room, RoomOutput> {
  @Override
  protected RoomOutput doConvert(Room source) {
    return RoomOutput.builder()
        .id(source.getId().toString())
        .floor(source.getFloor())
        .price(source.getPrice())
        .number(source.getNumber())
        .build();
  }
}
