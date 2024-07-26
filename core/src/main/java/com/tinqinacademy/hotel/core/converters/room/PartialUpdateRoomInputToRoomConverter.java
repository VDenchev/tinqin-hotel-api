package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartialUpdateRoomInputToRoomConverter extends BaseConverter<PartialUpdateRoomInput, Room> {

  private final RoomInputToRoomConverter roomInputToRoomConverter;

  @Override
  public Room doConvert(PartialUpdateRoomInput source) {
    Room room = roomInputToRoomConverter.convert(source.getRoomInput());
    room.setId(source.getRoomId());
    return room;
  }
}
